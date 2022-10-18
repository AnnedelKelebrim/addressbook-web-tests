package ru.stqa.pft.addressbook.appmanager;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import ru.stqa.pft.addressbook.model.ContactData;
import ru.stqa.pft.addressbook.model.Contacts;
import ru.stqa.pft.addressbook.model.GroupData;

import java.util.List;

public class ContactHelper extends HelperBase {
    private ApplicationManager app;

    public ContactHelper(WebDriver wd, ApplicationManager app) {
        super(wd);
        this.app = app;
    }

    public void submitContactCreation() {
        click(By.xpath("//div[@id='content']/form/input[21]"));
    }

    public void submitContactDeletion() {
        click(By.xpath("//input[@value='Delete']"));
    }

    public void submitContactEdition() {
        click(By.xpath("//div[@id='content']/form/input[22]"));
    }

    public void fillContactForm(ContactData contactData, boolean creation) {

        if (creation) {
            if (!isThereAGroupByName(contactData.getGroup())) {
                app.goTo().groupPage();
                app.group().create(new GroupData().withName(contactData.getGroup()).withHeader("skdjfh").withFooter("ajdhfbvlksjdhv"));
                app.goTo().createContactPage();
            }
            new Select(wd.findElement(By.name("new_group"))).selectByVisibleText(contactData.getGroup());
        } else {
            Assert.assertFalse(isElementPresent(By.name("new_group")));
        }

        type(By.name("firstname"), contactData.getFirstName());
        type(By.name("middlename"), contactData.getMiddleName());
        type(By.name("lastname"), contactData.getLastName());
        type(By.name("nickname"), contactData.getNickName());
        type(By.name("title"), contactData.getWho());
        type(By.name("company"), contactData.getCompany());
        type(By.name("address"), contactData.getFirstAddress());
        type(By.name("home"), contactData.getHomePhone());
        type(By.name("mobile"), contactData.getMobilePhone());
        type(By.name("work"), contactData.getWorkPhone());
        type(By.name("email"), contactData.getEmail());
        selectList(By.name("bday"), contactData.getBday());
        selectList(By.name("bmonth"), contactData.getBmonth());
        type(By.name("byear"), contactData.getByear());
        type(By.name("address2"), contactData.getSecondAddress());
        type(By.name("notes"), contactData.getNotes());
    }

    public void selectContactById(int id) {
        wd.findElement(By.cssSelector("input[id='" + id + "']")).click();
    }

    public void goToEditContactPage(int id) {
        click(By.xpath("//tr[@name='entry']/td[8]/a[@href='edit.php?id=" + id + "']"));
    }

    public void returnToHomePage() {
        click(By.linkText("home page"));
    }

    public void closeAlert() {
        wd.switchTo().alert().accept();
    }

    public boolean isThereAContact() {
        return isElementPresent(By.name("entry"));
    }

    public boolean isThereAGroupByName(String group) {
        return isElementPresent(By.xpath("//select[@name='new_group']/option[.='" + group + "']"));
    }

    public void create(ContactData contact) {
        fillContactForm(contact, true);
        submitContactCreation();
        contactCache = null;
        returnToHomePage();
    }

    public void delete(ContactData contact) {
        selectContactById(contact.getId());
        submitContactDeletion();
        contactCache = null;
        closeAlert();
    }

    public void modify(ContactData contact) {
        goToEditContactPage(contact.getId());
        fillContactForm(contact, false);
        submitContactEdition();
        contactCache = null;
        returnToHomePage();
    }

    public int count() {
        return wd.findElements(By.name("selected[]")).size();
    }

    private Contacts contactCache = null;

    public Contacts all() {
        if (contactCache != null) {
            return new Contacts(contactCache);
        }
        contactCache = new Contacts();
        List<WebElement> elements = wd.findElements(By.cssSelector("tr[name=\"entry\"]"));
        for (WebElement element : elements) {
            int id = Integer.parseInt(element.findElement(By.xpath("./td/input")).getAttribute("id"));
            String lastname = element.findElement(By.xpath("./td[2]")).getText();
            String firstname = element.findElement(By.xpath("./td[3]")).getText();
            String address = element.findElement(By.xpath("./td[4]")).getText();
            String allPhones = element.findElement(By.xpath("./td[6]")).getText();
            ContactData contact = new ContactData()
                    .withId(id)
                    .withFirstName(firstname)
                    .withLastName(lastname)
                    .withFirstAddress(address)
                    .withAllPhones(allPhones);
            contactCache.add(contact);
        }

        return new Contacts(contactCache);
    }

    public ContactData infoFromEditForm(ContactData contact) {
        goToEditContactPage(contact.getId());
        String firstName = wd.findElement(By.name("firstname")).getAttribute("value");
        String lastName = wd.findElement(By.name("lastname")).getAttribute("value");
        String home = wd.findElement(By.name("home")).getAttribute("value");
        String mobile = wd.findElement(By.name("mobile")).getAttribute("value");
        String work = wd.findElement(By.name("work")).getAttribute("value");
        String address = wd.findElement(By.cssSelector("textarea[name=\"address\"]")).getText();
        wd.navigate().back();
        return new ContactData()
                .withFirstName(firstName)
                .withLastName(lastName)
                .withHomePhone(home)
                .withMobilePhone(mobile)
                .withWorkPhone(work)
                .withFirstAddress(address);
    }
}
