package regEx ;

import java.util.Arrays ;

public class Matching {

public static int[] makeLps(String pattern) {
int n = pattern.length() ;
int[] lps = new int[n+1] ;
lps[0] = -1 ;

String[] PrefixList = new String[n-1] ;  
PrefixList[0]= pattern.substring(0,1) ;
System.out.println(PrefixList[0] );
for (int i=1 ;i< n-1 ; i++) {
PrefixList[i]=PrefixList[i-1]+pattern.substring(i,i+1) ;
System.out.println(PrefixList[i]) ;}

String dejaVu ="" ;
int i = 0 ;
String prefix = pattern.substring(i,i+1) ;


while(i<n) {
    System.out.println(i) ;
    String c = pattern.substring(i,i+1) ;
     if (dejaVu.contains(c) && (Arrays.asList(PrefixList).contains(prefix)) )  {
        lps[i+1]=prefix.length() ;
        prefix+=pattern.substring(i+1,i+2) ;
        i++ ;
    }
    else {
        prefix=pattern.substring(i+1,i+2);
        lps[i+1]=0 ;
        dejaVu+=pattern.charAt(i) ;
        i++ ;
    }
}
return lps ;
}

 
}