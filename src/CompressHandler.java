import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Stack;

public class CompressHandler{
    int index;
    int numberOfones=0;
    int size;
    //this function read from a file n bytes
    public  HashMap<ByteArrayWrapper, Long> readNbytes(File file,int n) throws IOException{
        FileInputStream fileInputStream = new FileInputStream(file);
        BufferedInputStream bufferedInputStream=new BufferedInputStream(fileInputStream,32000);
        //map of byte[] and integer for frequency to be calculated
        HashMap<ByteArrayWrapper, Long> ByteFrequency = new HashMap<>();
        //object that contains a byte[] attribute
        byte[] data=new byte[n];
        int bytesRead = 0;
        //read n bytes from file and store it in the array inside the object
        while ((bytesRead = bufferedInputStream.read(data)) != -1)
        {
            if(bytesRead<n){
                byte[] temp=new byte[bytesRead];
                System.arraycopy(data,0,temp,0,bytesRead);
                calculateFrequency(temp, ByteFrequency);
            }else{
                //function to calculate frequency of the chunk
                calculateFrequency(data, ByteFrequency);
            }
                //make new array for next chunck
                data=new byte[n];
        }
        bufferedInputStream.close();
        return ByteFrequency;
    }

    //this function saves the frequency of each byte in a hashmap
    private void calculateFrequency(byte[] bytes,HashMap<ByteArrayWrapper, Long> ByteFrequency) throws IOException {
        ByteArrayWrapper temp=new ByteArrayWrapper(bytes.clone());
        if(ByteFrequency.containsKey(temp))
            ByteFrequency.put(temp, ByteFrequency.get(temp) + 1);
        else
            ByteFrequency.put(temp, (long) 1);
    }

    public void FillingTree(HashMap<ByteArrayWrapper,Long> frequenciesMap , PriorityQueue<HuffmanNode> Tree){
        for (ByteArrayWrapper key : frequenciesMap.keySet() ){
            Tree.add(new HuffmanNode(key,frequenciesMap.get(key)));
        }
        constructTree(Tree);
    }

    public void constructTree(PriorityQueue<HuffmanNode> Nodes){
        int i=Nodes.size();
        while(i>1){
            HuffmanNode first=Nodes.poll();
            HuffmanNode second=Nodes.poll();
            long smallest2Frequency=first.getFrequency()+second.getFrequency();
            first.setCode("0");
            second.setCode("1");
            HuffmanNode current=new HuffmanNode(smallest2Frequency,first,second);
            Nodes.add(current);
            i--;
        }
    }

    //this function constructs the codewords and put it in a hashmap correspondig to each byte
    public  void ConstructCodeWords(HashMap<ByteArrayWrapper,String> CodeWordsMap,HuffmanNode root,String s){
        s=s+root.getCode();
        if(root.left==null && root.right==null){
            CodeWordsMap.put(root.value,s);
            root.setCode(s);
            return ;
        }
        else{
            ConstructCodeWords(CodeWordsMap,root.left,s);
            ConstructCodeWords(CodeWordsMap,root.right,s);
        }
    }

    public void compressingFile(File file ,HashMap<ByteArrayWrapper,String> CodeWordsMap,byte[] dictionary ,int n) throws IOException {
        String fileOld = file.getName();
        String compressedFile = file.getAbsolutePath().replace(fileOld,"18011648."+n+"."+fileOld+".hc");
        FileOutputStream fileOutputStream=new FileOutputStream(compressedFile);
        BufferedOutputStream bufferedOutputStream=new BufferedOutputStream(fileOutputStream);
        bufferedOutputStream.write(dictionary);
        writebody(file,bufferedOutputStream, n, CodeWordsMap);
    }

    public byte[] makingDictionary(HuffmanNode root,int n){
        Stack<HuffmanNode> stk = new Stack<>();
        stk.push(root);
        StringBuilder dictionary = new StringBuilder();

        while(!stk.isEmpty()){
            HuffmanNode temp = stk.pop();
            if(temp.left==null && temp.right==null)
            {   dictionary.append("1");
                numberOfones++;
                if(temp.value.data.length <n){
                    this.index=numberOfones;
                    this.size=temp.value.data.length;
                }
                dictionary.append(new String(temp.value.data,Charset.forName("ISO-8859-1")));}
            else
                dictionary.append("0");
            if(temp.right==null && temp.left != null )
                stk.push(temp.left);
            else if(temp.left ==null && temp.right !=null)
                stk.push(temp.right);
            else if(temp.left !=null && temp.right !=null){
                stk.push(temp.right);
                stk.push(temp.left);
            }
        }
        StringBuilder N_DLength_Dictionary=new StringBuilder();
        N_DLength_Dictionary.append(String.valueOf(n)+","+this.index+","+this.size);
        N_DLength_Dictionary.append(","+dictionary.length()+","+dictionary);
//        System.out.println("Left Side");
//        System.out.println("n: "+n);
//        System.out.println("Dictionary size: "+dictionary.length());
//        System.out.println("Dictionary: "+dictionary);
//        System.out.println("Dictionary All: "+N_DLength_Dictionary);
        byte[] dictionarybytes=N_DLength_Dictionary.toString().getBytes(Charset.forName("ISO-8859-1"));
        return dictionarybytes;
    }

//this function write to a file
public  void writebody(File file,BufferedOutputStream bufferedOutputStream,int n,HashMap<ByteArrayWrapper,String> CodeWordsMap) throws IOException{
    FileInputStream fileInputStream = new FileInputStream(file);
    BufferedInputStream bufferedInputStream=new BufferedInputStream(fileInputStream,32000);
    byte[] data=new byte[n];
    int bytesRead = 0;
    StringBuilder s=new StringBuilder();
    ByteArrayWrapper wrapper;
    while ((bytesRead = bufferedInputStream.read(data)) != -1)
    {
        if(bytesRead<n){
            byte[] temp=new byte[bytesRead];
            System.arraycopy(data,0,temp,0,bytesRead);
            wrapper=new ByteArrayWrapper(temp.clone());
        }else{
            wrapper = new ByteArrayWrapper(data.clone());
        }
        s.append(CodeWordsMap.get(wrapper));
        while(s.length()>=8){
            String StringToBeWritten = s.substring(0, 8);
            //write byte byte to a file
            bufferedOutputStream.write(((Integer)Integer.parseInt(StringToBeWritten,2)).byteValue());
            s.delete(0,8);
        }
        data=new byte[n];
    }

    //if there is still some bits remaining in string append to it zeros untill byte is full then write the byte
    int countZeros=0;
    if(s.length()>0){
      // System.out.println("There is some bits remaining");
        for(int i=s.length();i<8;i++){
            s.append("0");
            countZeros++;
        }
       // System.out.println("Added Zeros: "+countZeros);
        bufferedOutputStream.write(((Integer)Integer.parseInt(s.toString(),2)).byteValue());
    }
    bufferedOutputStream.write(((byte)countZeros));

    bufferedOutputStream.close();
    bufferedInputStream.close();
}
}