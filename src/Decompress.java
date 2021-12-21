import java.io.File;
import java.io.IOException;

public class Decompress {
    DecompressHandler decompressHandler=new DecompressHandler();
    public void decompress(File file) throws IOException {
       decompressHandler.readFromCompressed(file);
    }
}
