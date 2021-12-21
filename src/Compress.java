import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Stack;

public class Compress {
    CompressHandler compressHandler=new CompressHandler();
    public void compress(File file,int n) throws IOException{
        //make frequencies hashmap
        System.out.println("Value of N is : "+n);
        long start = System.currentTimeMillis();
        HashMap<ByteArrayWrapper,Long> frequenciesMap = compressHandler.readNbytes(file,n);
        long end = System.currentTimeMillis();
        System.out.println("COMPRESS : Calculating frequency Time: "+((end-start)));

        //making the tree
        PriorityQueue<HuffmanNode> Tree = new PriorityQueue<>(frequenciesMap.size(), new SortByFrequency());
        start = System.currentTimeMillis();
        compressHandler.FillingTree(frequenciesMap,Tree);
        end = System.currentTimeMillis();
        System.out.println("COMPRESS : Constructing Tree Time: "+((end-start)));

        HashMap<ByteArrayWrapper,String> CodeWordsMap= new HashMap<>();
        HuffmanNode root= Tree.peek();

        //putting codewords in the tree
        start = System.currentTimeMillis();
        //if file have only one character
        if(root.left==null && root.right==null){
            CodeWordsMap.put(root.value,"0");
        }else{
            compressHandler.ConstructCodeWords(CodeWordsMap,root,"");
        }
        end = System.currentTimeMillis();
        System.out.println("COMPRESS : CodeWords Time: "+((end-start)));

        //compressing the file with format --> n,sizeofdictionary,dictionary then file
        start = System.currentTimeMillis();
        compressHandler.compressingFile(file,CodeWordsMap,compressHandler.makingDictionary(root,n),n);
        end = System.currentTimeMillis();
        System.out.println("COMPRESS : Compressing Time: "+((end-start)));

        //printing
//        System.out.println("ByteArray: ");
//        for(ByteArrayWrapper x : CodeWordsMap.keySet()){
//            System.out.print(Arrays.toString(x.data));
//        }
//        System.out.println();
//        System.out.println("CodeWords: ");
//        System.out.println(CodeWordsMap.values());
//        System.out.println();
    }
}


