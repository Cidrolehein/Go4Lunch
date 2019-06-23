package com.gacon.julien.go4lunch;

import android.widget.TextView;

import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import com.gacon.julien.go4lunch.controller.activities.ProfileActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isOpen;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.core.AllOf.allOf;

/**
 * Testing Map Fragment, List View Fragment and Workmates on root view
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainFragmentTest {

    @Rule
    public ActivityTestRule<ProfileActivity> mActivityTestRule
            = new ActivityTestRule<>(ProfileActivity.class);

    @Before
    public void init() {
        mActivityTestRule.getActivity().getSupportFragmentManager().beginTransaction();
    }

    @Test
    public void TestBottomBarButtonsIsVisible() {
        // check if buttons are visible
        onView(withId(R.id.map_view)).check(matches((isDisplayed())));
        onView(withId(R.id.list_view)).check(matches((isDisplayed())));
        onView(withId(R.id.workmates)).check(matches((isDisplayed())));
    }

    @Test
    public void TitleOnToolBar() {
        // check the main title
        onView(allOf(instanceOf(TextView.class), withParent(withId(R.id.activity_main_toolbar))))
                .check(matches(withText("I'm Hungry !")));
    }

    @Test
    public void NavigationDrawer() {
        // Test the LogOut Button on Navigation Drawer
        onView(withId(R.id.profile_main_drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.profile_main_drawer_layout)).check(matches(isOpen()));
        onView(withId(R.id.profile_main_nav_view)).perform(NavigationViewActions.navigateTo(R.id.profile_logout));

    }

}
