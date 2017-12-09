package colinparrott.com.songle.menu;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MainActivityTest {

    // Checks getDifficulty method returns correct difficulty
    @Test
    public void getDifficulty() throws Exception
    {
        MainActivity a = new MainActivity();

        assertEquals(Difficulty.VERY_EASY, a.getDifficulty(0));
        assertEquals(Difficulty.EASY, a.getDifficulty(1));
        assertEquals(Difficulty.MODERATE, a.getDifficulty(2));
        assertEquals(Difficulty.HARD, a.getDifficulty(3));
        assertEquals(Difficulty.VERY_HARD, a.getDifficulty(4));
        assertEquals(Difficulty.VERY_EASY, a.getDifficulty(5));
    }

}