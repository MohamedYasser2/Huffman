import java.util.Comparator;
public class SortByFrequency implements Comparator<HuffmanNode> {
    public int compare(HuffmanNode a, HuffmanNode b)
    {
        if(a.getFrequency()==b.getFrequency())
           return 0;
        else if(a.getFrequency()>b.getFrequency())
            return 1;
        else
            return -1;
    }
}