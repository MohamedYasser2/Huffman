import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Main {
    public static void main(String args[]) throws IOException{

         // Passing the path to the file as a parameter
       long start;
       long end;
       long total=0;
        if(args[0].equals("c")){
            File file= new File(args[1]);
            Compress c = new Compress();
            start = System.currentTimeMillis();
            c.compress(file,Integer.parseInt(args[2]));
            end = System.currentTimeMillis();
            System.out.println("Compressing OverAll Time: "+((end-start)));
           // total += end-start;
        }
        else if(args[0].equals("d")){
            System.out.println();
            File compressedfile = new File(args[1]);
            Decompress d = new Decompress();
            start = System.currentTimeMillis();
            d.decompress(compressedfile);
            end = System.currentTimeMillis();
            System.out.println("Decompressing OverAll Time: "+((end-start)));
//            total +=(end-start);
//            System.out.println();
//            System.out.println("Total Time: "+total +" MilliSeconds");
//            System.out.println("Total Time: "+(total/1000) +" Seconds");
        }

    }
}
