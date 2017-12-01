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
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class NewGameAndResetCorrectUI {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);



    @Test
    public void newGameAndResetCorrectUI()
    {
        new UserPrefsManager(mActivityTestRule.getActivity().getApplicationContext()).setGameInProgress(false);

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
                allOf(withId(R.id.btnSave),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.custom),
                                        0),
                                1),
                        isDisplayed()));
        button2.check(matches(isDisplayed()));

        ViewInteraction button3 = onView(
                allOf(withId(R.id.btnResume),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.custom),
                                        0),
                                2),
                        isDisplayed()));
        button3.check(matches(isDisplayed()));

        ViewInteraction button4 = onView(
                allOf(withId(R.id.btnReset),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.custom),
                                        0),
                                3),
                        isDisplayed()));
        button4.check(matches(isDisplayed()));


        ViewInteraction button6 = onView(
                allOf(withId(R.id.btnReset), withText("Reset and quit"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.custom),
                                        0),
                                3),
                        isDisplayed()));
        button6.perform(click());

        ViewInteraction button7 = onView(
                allOf(withId(android.R.id.button1),
                        isDisplayed()));
        button7.check(matches(isDisplayed()));

        ViewInteraction button8 = onView(
                allOf(withId(android.R.id.button2),
                        isDisplayed()));
        button8.check(matches(isDisplayed()));


        ViewInteraction textView = onView(
                allOf(withId(android.R.id.message), withText("All current progress will be lost!"),
                        isDisplayed()));
        textView.check(matches(withText("All current progress will be lost!")));


        ViewInteraction appCompatButton = onView(
                allOf(withId(android.R.id.button1), withText("Yes")));
        appCompatButton.perform(scrollTo(), click());

        ViewInteraction textView3 = onView(
                allOf(withId(R.id.textView5), withText("Select Difficulty"),
                        isDisplayed()));
        textView3.check(matches(withText("Select Difficulty")));


        ViewInteraction textView5 = onView(
                allOf(withId(R.id.txt_Difficulty),
                        isDisplayed()));
        textView5.check(matches(isDisplayed()));

        ViewInteraction textView6 = onView(
                allOf(withId(R.id.txt_DifficultyDesc),
                        isDisplayed()));

        ViewInteraction seekBar = onView(
                allOf(withId(R.id.diffSeek),
                        isDisplayed()));
        seekBar.check(matches(isDisplayed()));

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
