package colinparrott.com.songle.resume;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import colinparrott.com.songle.R;
import colinparrott.com.songle.menu.MainActivity;
import colinparrott.com.songle.storage.UserPrefsManager;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

/**
 * Tests UI for creating a new game, saving and viewing saved game information dialog
 */

@LargeTest
@RunWith(AndroidJUnit4.class)
public class SaveGameAndViewInfoUICorrect {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void saveGameAndViewInfoUICorrect()
    {
        new UserPrefsManager(mActivityTestRule.getActivity().getApplicationContext()).setGameInProgress(false);

        ViewInteraction textView = onView(
                allOf(withId(R.id.txt_Difficulty), withText("VERY EASY"),
                        isDisplayed()));
        textView.check(matches(withText("VERY EASY")));


        ViewInteraction button = onView(
                allOf(withId(R.id.btn_Play),
                        isDisplayed()));
        button.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        pressBack();

        ViewInteraction button2 = onView(
                allOf(withId(R.id.btnSave), withText("Save and quit"),
                        isDisplayed()));
        button2.perform(click());

        ViewInteraction textView3 = onView(
                allOf(withId(R.id.txt_Difficulty), withText("VERY EASY"),
                        isDisplayed()));
        textView3.check(matches(withText("VERY EASY")));

        ViewInteraction button3 = onView(
                allOf(withId(R.id.btn_ViewInfo), withText("Save Info"),
                        isDisplayed()));
        button3.perform(click());

        ViewInteraction textView4 = onView(
                allOf(withId(R.id.textViewWordsLeft),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.custom),
                                        0),
                                4),
                        isDisplayed()));
        textView4.check(matches(isDisplayed()));

        ViewInteraction textView5 = onView(
                allOf(withId(R.id.textViewFoundWords), withText("0"),
                        isDisplayed()));
        textView5.check(matches(withText("0")));

        ViewInteraction textView6 = onView(
                allOf(withId(R.id.textViewSaveInfo), withText("Game information"),
                        isDisplayed()));
        textView6.check(matches(withText("Game information")));

        ViewInteraction textView7 = onView(
                allOf(withId(R.id.textViewPlayTime),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.custom),
                                        0),
                                6),
                        isDisplayed()));
        textView7.check(matches(isDisplayed()));

        ViewInteraction textView8 = onView(
                allOf(withId(R.id.textViewSaveTime),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.custom),
                                        0),
                                8),
                        isDisplayed()));
        textView8.check(matches(isDisplayed()));

        ViewInteraction textView9 = onView(
                allOf(withId(R.id.textViewSaveTime),
                        isDisplayed()));
        textView9.check(matches(isDisplayed()));

    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
