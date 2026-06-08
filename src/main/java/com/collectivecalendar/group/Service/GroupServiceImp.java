package com.collectivecalendar.group.Service;
import com.collectivecalendar.model.Group;
import com.collectivecalendar.model.User;
import com.collectivecalendar.model.UserGroup;
import com.collectivecalendar.repository.GroupRepository;
import com.collectivecalendar.repository.UserGroupRepository;
import com.collectivecalendar.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
public class GroupServiceImp implements GroupService {
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final UserGroupRepository userGroupRepository;

    public GroupServiceImp(GroupRepository groupRepository,
                           UserRepository userRepository,
                           UserGroupRepository userGroupRepository) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.userGroupRepository = userGroupRepository;
    }



    @Override
    public void editGroup(String group_id, String group_name) {
        UUID id = UUID.fromString(group_id);

        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Neatrada grupu: " + group_id));

        group.setName(group_name);

        groupRepository.save(group);
    }

    @Override
    @Transactional
    public void createGroup(String group_name, UUID currentUserId) {
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("Neatrada lietotāju: " + currentUserId));

        Group group = new Group();
        group.setName(group_name);
        Group savedGroup = groupRepository.save(group);

        UserGroup creatorMembership = UserGroup.builder()
                .userId(currentUser.getUid())
                .groupId(savedGroup.getUid())
                .role("EDITOR")
                .notify(true)
                .build();
        userGroupRepository.save(creatorMembership);
    }

    @Override
    @Transactional
    public void deleteGroup(String group_id) {
        UUID id = UUID.fromString(group_id);

        if (!groupRepository.existsById(id)) {
            throw new RuntimeException("Neatrada grupu:  " + group_id);
        }

        userGroupRepository.deleteByGroupId(id);
        groupRepository.deleteById(id);
    }

}
