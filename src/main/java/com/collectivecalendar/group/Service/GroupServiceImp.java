package com.collectivecalendar.group.Service;
import com.collectivecalendar.model.Group;
import com.collectivecalendar.repository.GroupRepository;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class GroupServiceImp implements GroupService {
    private final GroupRepository groupRepository;

    public GroupServiceImp(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
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
    public void createGroup(String group_name) {
        Group group = new Group();
        group.setName(group_name);
                groupRepository.save(group);
    }

    @Override
    public void deleteGroup(String group_id) {
        UUID id = UUID.fromString(group_id);

        if (!groupRepository.existsById(id)) {
            throw new RuntimeException("Neatrada grupu:  " + group_id);
        }

        groupRepository.deleteById(id);
    }

}
