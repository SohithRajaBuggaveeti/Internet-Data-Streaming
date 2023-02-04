public class Flow implements Comparable<Flow> {
    String flowId;
    String trueSpread;
    double estimatedSpread;
    int[] elements;
    public Flow(String flowId,String trueSpread)
    {
        this.flowId=flowId;
        this.trueSpread=trueSpread;
    }
    @Override
    public int compareTo(Flow other) {
        return ((int)this.estimatedSpread - (int)other.estimatedSpread); // or whatever property you want to sort
    }
}
