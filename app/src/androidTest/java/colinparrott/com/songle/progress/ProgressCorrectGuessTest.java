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
import colinparrott.com.songle.game.obj.GameStateKey;
import colinparrott.com.songle.game.obj.Song;
import colinparrott.com.songle.menu.MainActivity;
import colinparrott.com.songle.storage.UserPrefsManager;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

/**
 * Checks ProgessActivity correctly updates completed count TextView after a song is guessed
 */

@LargeTest
@RunWith(AndroidJUnit4.class)
public class ProgressCorrectGuessTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Rule
    public ActivityTestRule<ProgressActivity> mProgressActivityTestRule = new ActivityTestRule<>(ProgressActivity.class);

    @Test
    public void progressCorrectGuessTest() {

        Espresso.closeSoftKeyboard();

        ViewInteraction button = onView(
                allOf(ViewMatchers.withId(R.id.btn_Completed),
                        isDisplayed()));
        button.perform(click());

        // Get number of completed songs and total number of songs available
        UserPrefsManager userPrefsManager = new UserPrefsManager(mActivityTestRule.getActivity());
        int startingCompleted = userPrefsManager.getCompletedNumbersInt().length;
        int startingTotal = mProgressActivityTestRule.getActivity().getNumberOfSongs();

        pressBack();

        ViewInteraction button2 = onView(
                allOf(withId(R.id.btn_Play),
                        childAtPosition(
                                allOf(withId(R.id.constraint_layout),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                0),
                        isDisplayed()));
        button2.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        Song chosenSong;
        // Get chosen song for map
        if(mActivityTestRule.getActivity().getGameCreator() != null)
        {
            chosenSong = mActivityTestRule.getActivity().getGameCreator().getChosenSong();
        }
        else
        {
            UserPrefsManager u = new UserPrefsManager(mActivityTestRule.getActivity().getApplicationContext());
            chosenSong = u.retrieveObject(GameStateKey.SONG.name(), Song.class);
        }

        ViewInteraction button3 = onView(
                allOf(withId(R.id.btn_guess), withText("Guess"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        button3.perform(click());

        ViewInteraction editText = onView(
                allOf(withId(R.id.edit_guess),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.custom),
                                        0),
                                1),
                        isDisplayed()));
        editText.perform(replaceText(chosenSong.getTitle()), closeSoftKeyboard());

        ViewInteraction appCompatButton = onView(
                allOf(withId(android.R.id.button1), withText("Guess"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.buttonPanel),
                                        0),
                                3)));
        appCompatButton.perform(scrollTo(), click());

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(android.R.id.button1), withText("Ok"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.buttonPanel),
                                        0),
                                3)));
        appCompatButton2.perform(scrollTo(), click());


        Espresso.closeSoftKeyboard();

        ViewInteraction button4 = onView(
                allOf(withId(R.id.btn_Completed), withText("Progress"),
                        isDisplayed()));
        button4.perform(click());


        ViewInteraction textView_ = onView(
                allOf(withId(R.id.txtViewCompleted),
                        isDisplayed()));

        // Check TextView is correctly updated by 1 if guessed a song without having already guessed them all;
        // if not then make sure it's still the same as when we started
        if(startingCompleted < startingTotal)
        {
            textView_.check(matches(withText((startingCompleted + 1) + "/" + startingTotal + " Completed")));
        }
        else
        {
            textView_.check(matches(withText((startingCompleted) + "/" + startingTotal + " Completed")));
        }

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
