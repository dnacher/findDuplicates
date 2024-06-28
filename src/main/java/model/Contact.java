package model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Daniel Nacher
 * 2024-06-28
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Contact {
    private Integer id;
    private String firstName;
    private String lastName;
    private String emailAddress;
    private String zipcode;
    private String address;

}
