package service;

import model.Contact;
import model.ContactMatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Daniel Nacher
 * 2024-06-28
 */
public class FindDuplicatesService {

    /**
     * this values were created by checking some examples and thinking about how accurate was or not.
     * for me, the Email and address are the most important to think about a possible duplicate value
     * you could have a name and surname equals and this doesn't mean this person is the same person.
     *
     * i.e. We could have 'Daniel Nacher' living in Uruguay and another 'Daniel Nacher' living in Spain, this doesn't
     * mean are the same person and duplicated values.
     */
    private static final int NAME_MATCH_FULL = 12;
    private static final int NAME_MATCH_PARTIAL = 7;
    private static final int EMAIL_MATCH_FULL = 20;
    private static final int EMAIL_MATCH_PARTIAL = 17;
    private static final int ZIP_MATCH = 3;
    private static final int ADDRESS_MATCH_FULL = 20;
    private static final int ADDRESS_MATCH_PARTIAL = 17;
    private static final int ADDRESS_MISMATCH_PENALTY = -10;

    /**
     * Finds possible duplicate contacts from a given list of contacts.
     *
     * <p>This method compares each contact with every other contact in the list to calculate a
     * similarity score. If the similarity score exceeds a defined threshold (27), the contact pair
     * is considered a possible match and added to the result list.</p>
     *
     * @param contacts the list of contacts to be checked for duplicates
     * @return a list of {@link ContactMatch} objects representing possible duplicate contacts with
     *         their match accuracy
     */
    public static List<ContactMatch> findPossibleMatches(List<Contact> contacts) {
        List<ContactMatch> matches = new ArrayList<>();

        for (int i = 0; i < contacts.size(); i++) {
            for (int j = i + 1; j < contacts.size(); j++) {
                Contact c1 = contacts.get(i);
                Contact c2 = contacts.get(j);

                int score = calculateScore(c1, c2);
                if (score > 27) {
                    matches.add(new ContactMatch(c1.getId(), c2.getId(), getAccuracy(score)));
                }
            }
        }

        return matches;
    }

    /**
     * Calculates a similarity score between two contacts.
     *
     * <p>This method computes a score based on various attributes of the contacts such as
     * name, surname, email, zip code, and address. Each attribute contributes to the score
     * depending on how closely they match.</p>
     *
     * @param c1 the first contact to compare
     * @param c2 the second contact to compare
     * @return an integer score representing the similarity between the two contacts
     */
    private static int calculateScore(Contact c1, Contact c2) {
        int score = 0;

        score += matchNameAndSurname(c1.getFirstName(), c1.getLastName(), c2.getFirstName(), c2.getLastName());

        score += matchEmail(c1.getEmailAddress(), c2.getEmailAddress());

        score += matchZipCode(c1.getZipcode(), c2.getZipcode());

        score += matchAddress(c1.getAddress(), c2.getAddress());

        return score;
    }


    /**
     * Calculates a similarity score for the combination of first name and surname between two contacts.
     *
     * <p>This method evaluates the similarity of the first names and surnames separately and
     * sums their scores. If both the first names and surnames have a non-zero similarity score,
     * an additional bonus is added to the total score.</p>
     *
     * @param firstName1 the first name of the first contact
     * @param lastName1 the surname of the first contact
     * @param firstName2 the first name of the second contact
     * @param lastName2 the surname of the second contact
     * @return an integer score representing the similarity of the names and surnames between the two contacts
     */
    private static int matchNameAndSurname(String firstName1, String lastName1, String firstName2, String lastName2) {
        int nameScore = matchNameOrSurname(firstName1, firstName2, NAME_MATCH_FULL, NAME_MATCH_PARTIAL);
        int surnameScore = matchNameOrSurname(lastName1, lastName2, NAME_MATCH_FULL, NAME_MATCH_PARTIAL);
        int total = nameScore + surnameScore;
        if (nameScore > 0 && surnameScore > 0) {
            /* if the name and surname match at least partially this is more likely to be a duplicated than when the
            only one of these values matches */
            return total + 10;
        } else {
            return total;
        }
    }


    /**
     * Calculates a similarity score between two names or surnames.
     *
     * <p>This method compares two given names or surnames. If the names are exactly the same,
     * a full score is awarded. If one name is a single character and matches the start of the other name,
     * a partial score is awarded. If neither condition is met, a score of zero is returned.</p>
     *
     * @param name1 the first name or surname to compare
     * @param name2 the second name or surname to compare
     * @param fullScore the score awarded for an exact match
     * @param partialScore the score awarded for a partial match
     * @return an integer score representing the similarity between the two names or surnames
     */
    private static int matchNameOrSurname(String name1, String name2, int fullScore, int partialScore) {
        if (name1 != null && name2 != null) {
            if (name1.equalsIgnoreCase(name2)) {
                return fullScore;
            } else if (name1.length() == 1 && name2.startsWith(name1)) {
                return partialScore;
            } else if (name2.length() == 1 && name1.startsWith(name2)) {
                return partialScore;
            }
        }
        return 0;
    }

    /**
     * Calculates a similarity score between two email addresses.
     *
     * <p>This method compares two given email addresses. If the email addresses are exactly
     * the same, a full score (EMAIL_MATCH_FULL) is awarded. If the email addresses have the same
     * local part (part before '@'), a partial score (EMAIL_MATCH_PARTIAL) is awarded. If neither
     * condition is met or if either email address is null, a score of zero is returned.</p>
     *
     * @param email1 the first email address to compare
     * @param email2 the second email address to compare
     * @return an integer score representing the similarity between the two email addresses
     */
    private static int matchEmail(String email1, String email2) {
        if (email1 != null && email2 != null) {
            if (email1.equalsIgnoreCase(email2)) {
                return EMAIL_MATCH_FULL;
            } else {
                String[] emailParts1 = email1.split("@");
                String[] emailParts2 = email2.split("@");
                if (emailParts1.length > 0 && emailParts2.length > 0 && emailParts1[0].equals(emailParts2[0])) {
                    return EMAIL_MATCH_PARTIAL;
                }
            }
        }
        return 0;
    }

    /**
     * Calculates a similarity score between two zip codes.
     *
     * <p>This method compares two given zip codes. If the zip codes are exactly the same,
     * a score (ZIP_MATCH) is returned. If either zip code is null or they are not the same,
     * a score of zero is returned.</p>
     *
     * @param zip1 the first zip code to compare
     * @param zip2 the second zip code to compare
     * @return an integer score representing the similarity between the two zip codes
     */
    private static int matchZipCode(String zip1, String zip2) {
        if (zip1 != null && zip2 != null && zip1.equals(zip2)) {
            return ZIP_MATCH;
        }
        return 0;
    }

    /**
     * Calculates a similarity score between two addresses.
     *
     * <p>This method compares two given addresses. If the addresses are exactly the same,
     * a full match score (ADDRESS_MATCH_FULL) is returned. If one address contains the other,
     * a partial match score (ADDRESS_MATCH_PARTIAL) is returned. Otherwise, a mismatch penalty
     * score (ADDRESS_MISMATCH_PENALTY) is returned. If either address is null, a score of zero
     * is returned.</p>
     *
     * @param address1 the first address to compare
     * @param address2 the second address to compare
     * @return an integer score representing the similarity between the two addresses
     */
    private static int matchAddress(String address1, String address2) {
        if (address1 != null && address2 != null) {
            if (address1.equalsIgnoreCase(address2)) {
                return ADDRESS_MATCH_FULL;
            } else if (address1.contains(address2) || address2.contains(address1)) {
                return ADDRESS_MATCH_PARTIAL;
            } else {
                return ADDRESS_MISMATCH_PENALTY;
            }
        }
        return 0;
    }

    /**
     * Determines the accuracy level based on a given score.
     *
     * <p>This method categorizes a score into "High", "Medium", or "Low" accuracy levels
     * based on predefined thresholds.</p>
     *
     * @param score the score to determine the accuracy level
     * @return a string representing the accuracy level ("High", "Medium", or "Low")
     */
    private static String getAccuracy(int score) {
        if (score >= 40) {
            return "High";
        } else if (score >= 30) {
            return "Medium";
        } else {
            return "Low";
        }
    }

    /**
     * THIS IS ONLY FOR TESTING PURPOSES
     * Counts the occurrences of each accuracy level in a list of ContactMatch objects.
     *
     * <p>This method iterates through a list of ContactMatch objects and counts how many
     * matches fall into each accuracy level category ("High", "Medium", "Low"). It returns
     * a map where the keys are accuracy levels and the values are the counts.</p>
     *
     * @param matches the list of ContactMatch objects to count accuracy levels for
     * @return a map where keys are accuracy levels ("High", "Medium", "Low") and values are
     *         the counts of matches falling into each accuracy level
     */
    public static Map<String, Integer> countAccuracyLevels(List<ContactMatch> matches) {
        Map<String, Integer> accuracyCounts = new HashMap<>();
        accuracyCounts.put("High", 0);
        accuracyCounts.put("Medium", 0);
        accuracyCounts.put("Low", 0);

        for (ContactMatch match : matches) {
            String accuracy = match.getAccuracy();
            if (accuracyCounts.containsKey(accuracy)) {
                accuracyCounts.put(accuracy, accuracyCounts.get(accuracy) + 1);
            }
        }

        return accuracyCounts;
    }

}
