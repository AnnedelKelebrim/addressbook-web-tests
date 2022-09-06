package ru.stqa.pft.addressbook.tests;

import org.testng.annotations.Test;
import ru.stqa.pft.addressbook.appmanager.TestBase;
import ru.stqa.pft.addressbook.model.GroupData;


public class GroupCreationTests extends TestBase {

    @Test
    public void testGroupCreation() throws Exception {

        app.getNavigationHelper().goToGroupPage();
        app.getGroupHelper().createGroup(new GroupData("Test1", "Вася", "Петя"));
    }
}
