package ulits;

public class IsOS {
    public String isOs(){
        String osName=null;
        if (System.getProperty("os.name").indexOf("Windows")!=-1) {
//			System.out.println(System.getProperty("os.name"));
            osName = "win";
        }else if (System.getProperty("os.name").indexOf("Linux")!=-1) {
//			System.out.println(System.getProperty("os.name"));
            osName = "linux";
        }else {
            return null;
        }
        return osName;
    }
}
