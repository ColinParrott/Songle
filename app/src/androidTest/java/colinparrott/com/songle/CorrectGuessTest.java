package colinparrott.com.songle;


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

import colinparrott.com.songle.game.obj.Song;
import colinparrott.com.songle.menu.MainActivity;

import static android.support.test.espresso.Espresso.onView;
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
 * This test makes sure the correct guess dialog appears when the user correctly guesses the song title.
 */

@LargeTest
@RunWith(AndroidJUnit4.class)
public class CorrectGuessTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);



    @Test
    public void correctGuessTest() {


        ViewInteraction button = onView(
                allOf(withId(R.id.btn_Play), withText("Play"),
                        childAtPosition(
                                allOf(withId(R.id.constraint_layout),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                0),
                        isDisplayed()));
        button.perform(click());



        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // TODO: SOMEHOW FIX THIS FOR WHEN A GAME'S ALREADY IN PROGRESS
        // Get chosen song for map
        Song chosenSong = mActivityTestRule.getActivity().getGameCreator().getChosenSong();

        // Click guess button
        ViewInteraction button2 = onView(
                allOf(withId(R.id.btn_guess), withText("Guess"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        button2.perform(click());

        // Enter guess using song object
        ViewInteraction editText = onView(
                allOf(withId(R.id.edit_guess),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.custom),
                                        0),
                                1),
                        isDisplayed()));
        editText.perform(replaceText(chosenSong.getTitle()), closeSoftKeyboard());

        // Submit guess
        ViewInteraction appCompatButton = onView(
                allOf(withId(android.R.id.button1), withText("Guess"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.buttonPanel),
                                        0),
                                3)));
        appCompatButton.perform(scrollTo(), click());

        // Make sure "correct" text appears
        ViewInteraction textView = onView(
                allOf(withId(R.id.textView), withText("Correct!"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.custom),
                                        0),
                                0),
                        isDisplayed()));
        textView.check(matches(withText("Correct!")));

        // Check displayed song title is correct
        ViewInteraction textView2 = onView(
                allOf(withId(R.id.textViewTitle),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.custom),
                                        0),
                                2),
                        isDisplayed()));
        textView2.check(matches(withText(chosenSong.getTitle())));

        // Check displayed song artist is correct
        ViewInteraction textView3 = onView(
                allOf(withId(R.id.textViewArtist),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.custom),
                                        0),
                                4),
                        isDisplayed()));
        textView3.check(matches(withText(chosenSong.getArtist())));

        // Check displayed song URL is correct
        ViewInteraction textView4 = onView(
                allOf(withId(R.id.textViewURL),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.custom),
                                        0),
                                6),
                        isDisplayed()));
        textView4.check(matches(withText(chosenSong.getLink())));


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
