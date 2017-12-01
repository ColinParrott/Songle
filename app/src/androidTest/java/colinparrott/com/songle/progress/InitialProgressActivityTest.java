package colinparrott.com.songle.progress;


import android.support.test.espresso.Espresso;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.matcher.ViewMatchers;
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
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

/**
 * Checks progress activity displays correctly with no songs completed
 */

@LargeTest
@RunWith(AndroidJUnit4.class)
public class InitialProgressActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Rule
    public ActivityTestRule<ProgressActivity> mProgressActivityTestRule = new ActivityTestRule<>(ProgressActivity.class);


    @Test
    public void initialProgressActivityTest()
    {

        Espresso.closeSoftKeyboard();

        // Hit progress button
        ViewInteraction button = onView(
                allOf(ViewMatchers.withId(R.id.btn_Completed),
                        isDisplayed()));
        button.perform(click());

        // Make sure title displays correctly
        ViewInteraction textView = onView(
                allOf(withId(R.id.textView6), withText("SONG LIST"),
                        childAtPosition(
                                allOf(withId(R.id.completedLinearLayout),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                0),
                        isDisplayed()));
        textView.check(matches(withText("SONG LIST")));

        // Get number of completed songs and total number of songs available
        UserPrefsManager userPrefsManager = new UserPrefsManager(mActivityTestRule.getActivity());
        int numSongs = userPrefsManager.getCompletedNumbersInt().length;
        int totalSongs = mProgressActivityTestRule.getActivity().getNumberOfSongs();

        // Check text showing number completed out of total number songs available is correct
        ViewInteraction textView_ = onView(
                allOf(withId(R.id.txtViewCompleted),
                        isDisplayed()));
        textView_.check(matches(withText(numSongs + "/" + totalSongs + " Completed")));

        // Check switch is displayed
        ViewInteraction switch_ = onView(
                allOf(withId(R.id.switchSort),
                        isDisplayed()));
        switch_.check(matches(isDisplayed()));

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
