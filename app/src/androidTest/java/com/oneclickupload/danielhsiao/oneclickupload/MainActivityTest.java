package com.oneclickupload.danielhsiao.oneclickupload;

import android.os.SystemClock;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.Gravity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerActions.close;
import static android.support.test.espresso.contrib.DrawerActions.open;
import static android.support.test.espresso.contrib.DrawerMatchers.isClosed;
import static android.support.test.espresso.matcher.ViewMatchers.hasSibling;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

/**
 * Created by Daniel Hsiao on 2017-06-20.
 */

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
    @Rule
    public ActivityTestRule<MainActivity> mNewActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    /**
     * Check if Drawer Opens correctly and everything in it is displayed
     */
    @Test
    public void testDrawerOpen(){
        //First Check that the drawer is closed and then open it
        onView(withId(R.id.drawer_layout)).check(matches(isClosed(Gravity.LEFT))).perform(open());
        //Check to see if drawer and its buttons are displayed
        onView(withId(R.id.drawer_layout)).check(matches(isDisplayed()));
        onView(withId(R.id.buttonSetting)).check(matches(isDisplayed()));
        onView(withId(R.id.editProfileButton)).check(matches(isDisplayed()));
        onView(withId(R.id.addProfileButton)).check(matches(isDisplayed()));
        //Close the drawer and check it's closed
        onView(withId(R.id.drawer_layout)).perform(close()).check(matches(isClosed(Gravity.LEFT)));
    }



    /**
     * Full functionality check. Check that a new profile is created and displayed in the drawer
     */
    @Test
    public void testAddProfile(){
        onView(withId(R.id.drawer_layout)).check(matches(isClosed(Gravity.LEFT))).perform(open());
        onView(withId(R.id.addProfileButton)).perform(click());
        //Performing Addition of Test Profile
        onView(withId(R.id.editText)).perform(typeText("Automatic Test"));
        //Since there are multiple rowCheckBox views, we need to isolate one by providing more restriction (hasSibling of a view that contains "Facebook" text)
        onView(allOf(withId(R.id.rowCheckBox), hasSibling(withText("Facebook")))).perform(click());
        onView(withId(R.id.buttonSave)).perform(click());
        onView(withId(R.id.drawer_layout)).perform(open());
        //Again, multiple lblListHeader views, we want to find the one we just created with "Automatic Test" text
        onView(allOf(withId(R.id.lblListHeader), withText("Automatic Test"))).check(matches(isDisplayed()));
        onView(withId(R.id.drawer_layout)).perform(close());
    }

}
