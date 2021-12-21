import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;

public class DecompressHandler {
    int numberOfones=0;
    int DictionaryIndex=0;
    boolean check=false;
    HuffmanNode temproot;
    HuffmanNode root;
    //this function extracts the dictionary from the file and converts it to a string
    public void readFromCompressed(File file) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(file);
        BufferedInputStream bufferedInputStream=new BufferedInputStream(fileInputStream);

        StringBuilder numberofBytes = new StringBuilder();
        StringBuilder sizeOfDictionary=new StringBuilder();
        StringBuilder indexString=new StringBuilder();
        StringBuilder sizeString=new StringBuilder();
        StringBuilder dictionary=new StringBuilder();

        String StringTobeRead;
        byte[] temp = new byte[1];
        int bytesRead=0;
        while((bytesRead = bufferedInputStream.read(temp))!=-1){
            StringTobeRead =new String(temp,Charset.forName("ISO-8859-1"));
            if(StringTobeRead.equals(","))
                break;
            numberofBytes.append(StringTobeRead);
        }
        while((bytesRead = bufferedInputStream.read(temp))!=-1){
            StringTobeRead =new String(temp,Charset.forName("ISO-8859-1"));
            if(StringTobeRead.equals(","))
                break;
            indexString.append(StringTobeRead);
        }
        while((bytesRead = bufferedInputStream.read(temp))!=-1){
            StringTobeRead =new String(temp,Charset.forName("ISO-8859-1"));
            if(StringTobeRead.equals(","))
                break;
            sizeString.append(StringTobeRead);
        }
        while((bytesRead = bufferedInputStream.read(temp))!=-1){
             StringTobeRead =new String(temp,Charset.forName("ISO-8859-1"));
            if(StringTobeRead.equals(","))
                break;
            sizeOfDictionary.append(StringTobeRead);
        }
        int n = Integer.parseInt(numberofBytes.toString());
        int index=Integer.parseInt(indexString.toString());
        int size=Integer.parseInt(sizeString.toString());
        int sizeofDictionary=Integer.parseInt(sizeOfDictionary.toString());

        temp=new byte[sizeofDictionary];
        bufferedInputStream.read(temp);
        StringTobeRead =new String(temp,Charset.forName("ISO-8859-1"));
        dictionary.append(StringTobeRead);

//       System.out.println("Right Side");
//       System.out.println("n: "+n);
//        System.out.println("Index: "+index);
//        System.out.println("Size: "+size);
//        System.out.println("Dictionary size: "+sizeofDictionary);
//       System.out.println("Dictionary: "+dictionary);

        //make the tree
        long start = System.currentTimeMillis();
        this.root=ConstructTree(dictionary,n,index,size);
        if(this.root.left==null && this.root.right==null){
            this.check=true;
        }
        this.temproot=root;
        long end = System.currentTimeMillis();
        System.out.println("DECOMPRESS : Construct Tree Time: "+((end-start)));


        //function to deecompress body of file
        start = System.currentTimeMillis();
        decompressBody(bufferedInputStream,n,file);
        end = System.currentTimeMillis();
        System.out.println("DECOMPRESS : Decompress Time: "+(end-start));
    }

    public void decompressBody(BufferedInputStream bufferedInputStream,int n,File file) throws IOException {
        String b = "extracted."+file.getName().replace(".hc","");
        FileOutputStream fileOutputStream = new FileOutputStream(file.getAbsolutePath().replace(file.getName(),b));
       BufferedOutputStream bufferedOutputStream =new BufferedOutputStream(fileOutputStream);
       byte[] buffer=new byte[32000];
       StringBuilder stringOfBits= new StringBuilder();
       int bytesRead=0;
       int shift=0;
        long startbig = System.currentTimeMillis();
       while((bytesRead = bufferedInputStream.read(buffer)) != -1){

           if(bytesRead<32000){
               byte[] temp = new byte[bytesRead];
               System.arraycopy(buffer,0,temp,0,bytesRead);
               for(int i=0;i<temp.length;i++)
                   stringOfBits.append(String.format("%8s", Integer.toBinaryString(temp[i] & 0xFF)).replace(' ', '0'));
           }

           else{
               for(int i=0;i<buffer.length;i++)
                   stringOfBits.append(String.format("%8s", Integer.toBinaryString(buffer[i] & 0xFF)).replace(' ', '0'));
           }
           if(bufferedInputStream.available()==1){
               byte[] ZerosByte=new byte[1];
               bufferedInputStream.read(ZerosByte);
               String st = String.format("%8s", Integer.toBinaryString(buffer[0] & 0xFF)).replace(' ', '0');
               shift = Integer.parseInt(st, 2);
               stringOfBits = new StringBuilder(stringOfBits.substring(0, stringOfBits.length()- shift));
           }
           else if(bufferedInputStream.available()==0){
               StringBuilder ZerosByte=new StringBuilder(stringOfBits.substring(stringOfBits.length()-8, stringOfBits.length()));
               shift = Integer.parseInt(ZerosByte.toString(), 2);
               stringOfBits = new StringBuilder(stringOfBits.substring(0, stringOfBits.length()-shift-8));
           }
           helper(bufferedOutputStream,stringOfBits);
           stringOfBits = new StringBuilder();
           buffer=new byte[32000];

       }
        long endbig = System.currentTimeMillis();
        System.out.println("DECOMPRESS : The big Loop: "+((endbig-startbig)));

        bufferedInputStream.close();
       bufferedOutputStream.close();
    }

    private void helper(BufferedOutputStream bufferedOutputStream,StringBuilder stringOfBits) throws IOException {
       // System.out.println("String of Bits: "+stringOfBits);
        for(int i=0;i<stringOfBits.length();i++){
            if(stringOfBits.charAt(i)=='0' && !this.check)
                this.temproot=this.temproot.left;
            else if(stringOfBits.charAt(i)=='1')
                this.temproot=this.temproot.right;
            if(this.temproot.left==null && this.temproot.right==null){
                bufferedOutputStream.write(this.temproot.value.data);
                this.temproot=this.root;
            }
        }

    }

//this function constructs the tree
    private HuffmanNode ConstructTree(StringBuilder dictionary,int n,int index,int size) {
        //parses the dictionary from the file and see
        //if the character is 1 then it is a leaf and get the bytes of ByteArrayWrapper which of size n
        if (dictionary.charAt(DictionaryIndex) == '1') {
            numberOfones++;
            byte[] bytes;
            if(numberOfones==index){
                bytes=dictionary.substring(DictionaryIndex+1,DictionaryIndex+size+1).getBytes(Charset.forName("ISO-8859-1"));
                // dictionary.delete(0,size+1);
                DictionaryIndex+=(size+1);
            }else{
                bytes=dictionary.substring(DictionaryIndex+1,DictionaryIndex+n+1).getBytes(Charset.forName("ISO-8859-1"));
                //dictionary.delete(0,n+1);
                DictionaryIndex+=(n+1);
            }
            return new HuffmanNode(new ByteArrayWrapper(bytes.clone()), null, null);
        }
        //if the character is 0 then it is not leaf then call left right then return a new node and put left right in it
        else {
            //dictionary.delete(0,1);
            DictionaryIndex++;
            HuffmanNode leftChild = ConstructTree(dictionary,n,index,size);
            HuffmanNode rightChild = ConstructTree(dictionary,n,index,size);
            return new HuffmanNode(null, leftChild, rightChild);
        }
    }
}
