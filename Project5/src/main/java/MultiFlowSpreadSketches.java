
import java.io.*;
import java.util.*;

public class MultiFlowSpreadSketches {
    public static void main(String args[]) throws IOException {

        Scanner s = new Scanner(System.in);

        System.out.println("Enter the Multi-Flow Spread Sketch type : ");
        String sketchType = s.next();
        int virtualbitMapSize;
        int bitMapSize;
        Flow[] flows = getFlowIds();
        switch (sketchType.toUpperCase().trim()) {
            case "VIRTUAL_BITMAP":
                System.out.println("Enter the no of bits in the Virtual BitMap ");
                virtualbitMapSize = s.nextInt();
                System.out.println("Enter the no of bits in the BitMap ");
                bitMapSize=s.nextInt();
                generateVirtualBitmap(flows,virtualbitMapSize,bitMapSize);
                break;
            case "BSKT":
                System.out.println("Enter the no of HLL estimators ");
                int noOfEstimators = s.nextInt();
                System.out.println("Enter the no of registers in each estimator ");
                int registerCount=s.nextInt();
                System.out.println("Enter the noof estimators each flow is hashed to : ");
                int noOfHashes=s.nextInt();
                generateBSKTBitmap(flows,noOfEstimators,registerCount,noOfHashes);
                break;
        }
    }

    private static void generateBSKTBitmap(Flow[] flows, int noOfEstimators, int registerCount,int noOfHashes) {
        int[] randomArray=getElements(noOfHashes);
        int[][] estimatorArray=new int[noOfEstimators][registerCount];
        for(Flow f :flows)
        {
            for(int i=0;i<noOfHashes;i++)
            {
                int indx=Math.abs(f.flowId.hashCode()^randomArray[i])%noOfEstimators;
                int[] elements=getElements(Integer.parseInt(f.trueSpread));
                for (int j = 0; j < elements.length; j++) {
                    long hashValue=Long.valueOf(elements[j]).hashCode();
                    int zeros=Long.numberOfLeadingZeros(hashValue%(long)Math.pow(2,32))%32;
                    hashValue=Math.abs(hashValue);
                    estimatorArray[indx][(int)hashValue%registerCount]=Math.max(estimatorArray[indx][(int)hashValue%registerCount],zeros+1);;

                    }
            }

        }
        generateEstimatedSpread(flows,registerCount,randomArray,noOfEstimators,estimatorArray);
        Arrays.sort(flows,Collections.reverseOrder());
        StringBuilder sb=new StringBuilder();
        for(int i=0;i<25;i++) {
            sb.append("Estimated Spread : "+Math.round(flows[i].estimatedSpread)+"\n");
        }
        generateOutputFile("bkst.txt",sb);
    }

    private static void generateEstimatedSpread(Flow[] flows,int registerCount,int[] randomArray,int noOfEstimators,int[][] estimatorArray) {
        double alpha=0.7213/(1+(1.079/(double)registerCount));
        for(Flow f :flows)
        {
            double min=Double.MAX_VALUE;
            for(int i=0;i<randomArray.length;i++)
            {
                double sum=0;
                int indx=Math.abs(f.flowId.hashCode()^randomArray[i])%noOfEstimators;
                for(int j=0;j<registerCount;j++)
                {
                    sum+=1.0/Math.pow(2,estimatorArray[indx][j]);
                }
                double estimatedSpread=alpha*(registerCount*registerCount)*1/sum;
                min=Math.min(min,estimatedSpread);
            }
            f.estimatedSpread=min;
        }
    }

    private static void generateVirtualBitmap(Flow[] flows, int virtualbitMapSize,int bitMapSize) {

        int[] randomArray=new int[virtualbitMapSize];
        for(int i=0;i<virtualbitMapSize;i++)
        {
            randomArray[i]=(int) (Math.random() * Integer.MAX_VALUE) + 1;
        }
        int[] bitMapArray=new int[bitMapSize];
        for(Flow f : flows) {
            int elementSize = Integer.parseInt(f.trueSpread.trim());
            int[] elements = getElements(elementSize);
            f.elements=elements;
            for (int i = 0; i < elements.length; i++)
            {
                int indx=Math.abs(Integer.valueOf(elements[i]).hashCode())%virtualbitMapSize;
                indx=String.valueOf(f.flowId.hashCode() ^Integer.hashCode(randomArray[indx])).hashCode()%bitMapSize;
                indx=Math.abs(indx);
                bitMapArray[indx]=1;
            }
          ;
        }
        getEstimatedSpread(bitMapArray,flows,randomArray,virtualbitMapSize);
        StringBuilder sb=new StringBuilder();
        for(int i=0;i<flows.length;i++) {
            sb.append(flows[i].trueSpread+","+Math.round(flows[i].estimatedSpread)+"\n");
        }
        generateOutputFile("virtual_bitmap.txt",sb);
    }

    private static void generateOutputFile(String fileName, StringBuilder sb) {

        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(fileName));
            bw.write(String.valueOf(sb));
            bw.close();
        } catch (Exception ex) {
            System.out.println("Exception in file creation");
        }

    }
    private static void getEstimatedSpread(int[] bitMapArray, Flow[] flows,int[] randomArray,int virtualBitMapSize) {

        double vb=0;
            for(int i=0;i<bitMapArray.length;i++)
            {
                if(bitMapArray[i]==0)
                {
                    vb++;
                }
            }
            vb=vb/bitMapArray.length;
             for(Flow f : flows)
             {
              double vf=0;
              for(int i=0;i<randomArray.length;i++)
              {
                  int indx=Math.abs(String.valueOf(f.flowId.hashCode() ^Integer.hashCode(randomArray[i])).hashCode())%bitMapArray.length;

                  if(bitMapArray[indx]==0) {
                      vf++;
                  }
              }
              vf=vf/500;
              f.estimatedSpread=virtualBitMapSize*(Math.log(vb)-Math.log(vf));
              if(f.estimatedSpread<0)
              {
                  f.estimatedSpread=0;
              }
             }
    }

    private static int[] getElements(int elementSize) {
        int[] s = new int[elementSize];
        //Using hashset to generate the unique values
        HashSet<Integer> hs = new HashSet<>();
        int i = 0;
        while (i < elementSize) {
            int temp = (int) (Math.random() * Integer.MAX_VALUE) + 1;
            if (!hs.contains(temp)) {
                s[i] = temp;
                i++;
            }
            hs.add(temp);
        }
        return s;
    }

    private static Flow[] getFlowIds() throws IOException {

        File file = new File("project5input.txt");
        BufferedReader br = new BufferedReader(new FileReader(file));
        int flowSize = Integer.parseInt(br.readLine());
        Flow f[] = new Flow[flowSize];
        String str;
        int i = 0;
        while ((str = br.readLine()) != null) {
            String[] st = str.split("\\t+");
            f[i++] = new Flow(st[0], st[1]);
        }
        return f;
    }
}
