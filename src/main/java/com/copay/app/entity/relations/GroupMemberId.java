package com.copay.app.entity.relations;

import java.io.Serializable;
import java.util.Objects;

import com.copay.app.entity.Group;
import com.copay.app.entity.User;

import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

/**
 * @Embeddable class representing the composite key for the 'GroupMember' 
 * entity, centralizing the many-to-one relationships between 'Group' and 'User'.
 * 
 * This class simplifies the many-to-many relationship by:
 * - Managing the relationships within the composite key itself.
 * - Allowing 'User' and 'Group' to reference the relationship through 'id.group' and 'id.user'.
 * - The 'GroupMember' entity uses the embedded key (GroupMemberId) to handle the many-to-one
 *   mappings without redundant definitions in the entity class.
 * 
 * Advantages:
 * - Cleaner and more maintainable mapping.
 * - Reduces redundancy in database relationships and entity definitions.
 */
@Embeddable
public class GroupMemberId implements Serializable {

	private static final long serialVersionUID = 1L;

	@ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Constructor used to create composite key using 'group' and 'user'.
    public GroupMemberId(Group group, User user) {
        this.group = group;
        this.user = user;
    }

    // Empty constructor.
    public GroupMemberId() {
    	
    }
    
    // Getters and Setters.
    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    // HashCode and  Equals.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupMemberId that = (GroupMemberId) o;
        return Objects.equals(group, that.group) && Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(group, user);
    }
}
