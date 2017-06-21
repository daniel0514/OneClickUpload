package com.oneclickupload.danielhsiao.oneclickupload;

import android.support.test.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;
import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by Daniel Hsiao on 2017-06-20.
 */

public class DatabaseHelperTest {
    private DatabaseHelper db;
    @Before
    public void setUp(){
        db = new DatabaseHelper(InstrumentationRegistry.getTargetContext());
    }

    @Test
    public void testAccess(){
        assertNotNull(db);
    }

    @Test
    public void truncateWholeDatabase(){
        db.deleteDatabase();
        List<Profile> profiles = db.getProfiles();
        assertThat(profiles.size(), is(0));
    }
    @Test
    public void addProfile() throws Exception {
        db.addProfile(new Profile("test", "test"));
        List<Profile> profiles = db.getProfiles();
        assertThat(profiles.size(), is(1));
        assertTrue(profiles.get(0).getName().equals("test"));
        assertTrue(profiles.get(0).getText().equals("test"));
        db.deleteDatabase();
    }

    @Test
    public void deleteNewlyCreatedProfile() throws Exception {
        db.addProfile(new Profile("test1", "test1"));
        List<Profile> profiles = db.getProfiles();
        db.removeProfile(profiles.get(0));
        List<Profile> noProfiles = db.getProfiles();
        assertThat(noProfiles.size(), is(0));
    }

    @Test
    public void addAccountToProfile() throws Exception {
        db.addProfile(new Profile("Porfile without Account", "Test"));
        Profile p = db.getProfiles().get(0);
        Account a = new Account(1, "", "");
        db.addAccount(a, p.getProfileID());
        Profile pWithAccount = db.getProfileByID(p.getProfileID());
        assertTrue(pWithAccount.getAccounts().size() == 1);
        assertTrue(db.getAccountsByProfileID(pWithAccount.getProfileID()).size() == 1);
        db.deleteDatabase();
    }


}
