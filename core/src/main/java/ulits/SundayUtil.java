package ulits;
/*
* 命名匹配
* */
public class SundayUtil {
    private   int MAXSIZE = 1024;
    private   int moveLength[]=null;
    public static void main(String[] agrs){
        SundayUtil sundayUtil=new SundayUtil();
    }
    public  int sunday(String haystack, String needle) {
        getMoveLength(needle);
        int sLen=haystack.length();
           int tLen=needle.length();
        int i = 0;
        while(i < sLen) {
            int j = 0;
        for(  ; j < tLen && i + j < sLen && haystack.charAt(i + j) == needle.charAt(j); ++j) ;

            if(j >= tLen) return i;
            if(i + tLen >= sLen)
            return -1;
            i += moveLength[haystack.charAt(i+tLen)];
        }
        return -1;
    }

    public  void getMoveLength(String p){
        moveLength=new int[MAXSIZE];
       int pLen=p.length();
       for (int i=0;i<MAXSIZE;i++){
           moveLength[i] = pLen + 1;
       }
       for (int i=0;i<p.length();i++){
           moveLength[p.charAt(i)] = pLen-i;
       }
    }

}

