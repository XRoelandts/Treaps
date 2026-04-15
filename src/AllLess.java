import java.util.*;
public class AllLess {

    public static void main(String[] args) {
        String[] s = {
                "zero", " size", "nutella", "jojo", "luna",
                "isse", "astor", "as", "entretien", "", "cal"
        };
        int x = 3;

        List<String> result = allLess(s, x);
        System.out.println(result);
        System.out.println(allLess(s, 5));
    }

    public static List<String> allLess(String[] s, int x) {
        List<String> result = new ArrayList<>();

        if (s == null || s.length == 0) return result;

        for (String str : s) {
            // Guard against null entries per the constraints
            if (str != null && str.length() < x) {
                result.add(str);
            }
        }

        return result;
    }
}
