package com.oneclickupload.danielhsiao.oneclickupload;

import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.hasSibling;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;

/**
 * Created by Daniel Hsiao on 2017-06-20.
 */

@RunWith(AndroidJUnit4.class)
public class NewProfileActivityTest {
    @Rule
    public ActivityTestRule<NewProfileActivity> mNewActivityTestRule = new ActivityTestRule<NewProfileActivity>(NewProfileActivity.class);

    @Test
    public void clickCancelButton() throws Exception{
        onView(withId(R.id.buttonCancel)).perform(click());
        onView(withText("Are you sure you want to close this activity without saving changes?")).check(matches(isDisplayed()));
    }
    @Test
    public void clickSaveButton() throws Exception{
        onView(withId(R.id.buttonSave)).perform(click());
        onView(withText("Please enter all required fields"))
                .inRoot((withDecorView(not(mNewActivityTestRule.getActivity().getWindow().getDecorView()))))
                .check(matches(isDisplayed()));
        onView(withId(R.id.editText)).perform(typeText("Automatic Test"));
        onView(withId(R.id.buttonSave)).perform(click());
        onView(withText("Please enter all required fields"))
                .inRoot((withDecorView(not(mNewActivityTestRule.getActivity().getWindow().getDecorView()))))
                .check(matches(isDisplayed()));
        onView(allOf(withId(R.id.rowCheckBox), hasSibling(withText("Facebook")))).perform(click());
        onView(withId(R.id.buttonSave)).perform(click());
    }


}
