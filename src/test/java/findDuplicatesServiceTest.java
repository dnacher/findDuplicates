import model.Contact;
import model.ContactMatch;
import org.testng.annotations.Test;
import service.ContactService;
import service.FindDuplicatesService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.testng.AssertJUnit.assertEquals;

/**
 * Daniel Nacher
 * 2024-06-28
 */
public class findDuplicatesServiceTest {

    @Test
    public void test() {
        List<Contact> contacts = ContactService.getContacts();
        List<ContactMatch> matches = FindDuplicatesService.findPossibleMatches(contacts);
        Map<String, Integer> contactsMatches = FindDuplicatesService.countAccuracyLevels(matches);
        assertEquals(648, matches.size());
        assertEquals(Optional.of(540).get(), contactsMatches.get("High"));
        assertEquals(Optional.of(52).get(), contactsMatches.get("Medium"));
        assertEquals(Optional.of(56).get(), contactsMatches.get("Low"));
    }

    @Test
    public void test2() {
        List<Contact> contacts = new ArrayList<>();
        contacts.add(new Contact(1001, "C", "F", "mollis.lectus.pede@outlook.net", null, "449-6990 Tellus. Rd."));
        contacts.add(new Contact(1002, "C", "French", "mollis.lectus.pede@outlook.net", "39746", "449-6990 Tellus. Rd."));
        contacts.add(new Contact(1003, "Ciara", "F", "non.lacinia.at@zoho.ca", "39746", null));

        List<ContactMatch> matches = FindDuplicatesService.findPossibleMatches(contacts);
        Map<String, Integer> contactsMatches = FindDuplicatesService.countAccuracyLevels(matches);
        assertEquals(2, matches.size());
        assertEquals(Optional.of(1).get(), contactsMatches.get("High"));
        assertEquals(Optional.of(0).get(), contactsMatches.get("Medium"));
        assertEquals(Optional.of(1).get(), contactsMatches.get("Low"));
        for (ContactMatch match : matches) {
            System.out.println(match);
        }
    }

}
