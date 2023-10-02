package regEx;

import static org.junit.Assert.assertEquals;

public class Tests {

  Automate auto;
  public Tests() {}

  private void checkMatches(String regEx, String toTest,
                            Pair<Integer, Integer> expected) throws Exception {
    auto = Automate.buildFromRegex(regEx);
    assertEquals(auto.getFirstMatchWithIndex(toTest), expected);
  }

  @org.junit.Test
  public void SimpleA() throws Exception {
    checkMatches("a", "a", new Pair<Integer, Integer>(0, 1));
  }

  @org.junit.Test
  public void SimpleNoMatch() throws Exception {
    checkMatches("a", "b", null);
  }

  @org.junit.Test
  public void SimpleAB() throws Exception {
    checkMatches("ab", "ab", new Pair<Integer, Integer>(0, 2));
  }

  @org.junit.Test
  public void SimpleABNoMatch() throws Exception {
    checkMatches("ab", "a", null);
  }

  @org.junit.Test
  public void SimpleABNoMatch2() throws Exception {
    checkMatches("ab", "b", null);
  }

  @org.junit.Test
  public void SimpleABMatch3() throws Exception {
    checkMatches("ab", "ab ab ab", new Pair<Integer, Integer>(0, 2));
  }

  @org.junit.Test
  public void AdvencedAB() throws Exception {
    checkMatches("ab", "ab | ab –dfsfsdfdsdf ab",
                 new Pair<Integer, Integer>(0, 2));
  }

  @org.junit.Test
  public void SimpleOr() throws Exception {
    checkMatches("a|b", "ab", new Pair<Integer, Integer>(0, 1));
  }

  @org.junit.Test
  public void Or2() throws Exception {
    checkMatches("ca|aaab", "aaaabc", new Pair<Integer, Integer>(1, 5));
  }

  @org.junit.Test
  public void Or3() throws Exception {
    checkMatches("ca|aaab", "caaaab", new Pair<Integer, Integer>(0, 2));
  }

  @org.junit.Test
  public void advencedOr() throws Exception {
    checkMatches(".a|bc", "abccccbcc", new Pair<Integer, Integer>(1,3));
  }
}