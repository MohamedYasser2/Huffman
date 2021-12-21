public class HuffmanNode {
    private long frequency;
    ByteArrayWrapper value;
    private String code="";
    String actualCharacter;
    HuffmanNode left;
    HuffmanNode right;
    public HuffmanNode(){}
    public HuffmanNode(ByteArrayWrapper value,HuffmanNode left,HuffmanNode right){
        this.value=value;
        this.left=left;
        this.right=right;
    }
    public HuffmanNode(ByteArrayWrapper val,long frequency){
        this.value=val;
        this.frequency=frequency;
    }
    public HuffmanNode(long frequency,HuffmanNode l,HuffmanNode r){
        this.frequency=frequency;
        this.left=l;
        this.right=r;
    }
    public void setCode(String code){
        this.code =code;
    }
    public String getCode(){
        return this.code;
    }
    public long getFrequency(){
        return this.frequency;
    }
}
