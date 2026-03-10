package com.example.tpms.service;

import com.example.tpms.dto.*;
import com.example.tpms.entity.UserProfile;
import com.example.tpms.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public UserProfile saveUserFromJson(String jsonString) throws Exception {
        UserProfile userProfile = objectMapper.readValue(jsonString, UserProfile.class);
        return repository.save(userProfile);
    }
    public UserProfileDto getUserProfileComplete(Long userId) {
        MapSqlParameterSource params = new MapSqlParameterSource("id", userId);

        // 1. Basic Profile
        String profileSql = "SELECT * FROM user_profile WHERE id = :id";
        UserProfileDto dto = jdbcTemplate.queryForObject(profileSql, params, (rs, rowNum) -> {
            UserProfileDto p = new UserProfileDto();
            p.setId(rs.getLong("id"));
            p.setName(rs.getString("name"));
            p.setTitle(rs.getString("title"));
            p.setEmail(rs.getString("email"));
            p.setPhone(rs.getString("phone"));
            p.setLocation(rs.getString("location"));
            p.setLinkedin(rs.getString("linkedin"));
            p.setGithub(rs.getString("github"));
            p.setProfessionalSummary(rs.getString("professional_summary"));
            return p;
        });

        if (dto != null) {
            dto.setEducation(fetchEducation(userId));
            dto.setExperience(fetchExperience(userId));
            dto.setProjects(fetchProjects(userId));
            dto.setSkills(fetchSkills(userId));
        }
        return dto;
    }

    // 2. Education (Using Join Table Query)
    private List<EducationDTO> fetchEducation(Long userId) {
        String sql = "SELECT * FROM user_education WHERE id IN " +
                "(SELECT education_id FROM user_profile_education WHERE user_profile_id = :id)";
        return jdbcTemplate.query(sql, new MapSqlParameterSource("id", userId), (rs, rowNum) -> {
            EducationDTO edu = new EducationDTO();
            edu.setInstitution(rs.getString("institution"));
            edu.setDegree(rs.getString("degree"));
            edu.setCompletionYear(rs.getString("completion_year"));
            edu.setScore(rs.getString("score"));
            return edu;
        });
    }

    // 3. Experience & Highlights (Handling Join Rows)
    private List<ExperienceDTO> fetchExperience(Long userId) {
        String sql = "SELECT e.*, h.highlights FROM experience e " +
                "LEFT JOIN experience_highlights h ON e.id = h.experience_id " +
                "WHERE e.id IN (SELECT experience_id FROM user_profile_experience WHERE user_profile_id = :id)";

        return jdbcTemplate.query(sql, new MapSqlParameterSource("id", userId), rs -> {
            Map<Long, ExperienceDTO> map = new LinkedHashMap<>();
            while (rs.next()) {
                Long id = rs.getLong("id");
                ExperienceDTO exp = map.getOrDefault(id, new ExperienceDTO());
                if (exp.getCompany() == null) {
                    exp.setCompany(rs.getString("company"));
                    exp.setRole(rs.getString("role"));
                    exp.setDuration(rs.getString("duration"));
                    exp.setHighlights(new ArrayList<>());
                    map.put(id, exp);
                }
                String highlight = rs.getString("highlights");
                if (highlight != null) exp.getHighlights().add(highlight);
            }
            return new ArrayList<>(map.values());
        });
    }

    // 4. Projects (Handling Multiple Joins)
    private List<ProjectDTO> fetchProjects(Long userId) {
        String sql = "SELECT p.*, pt.technology, pd.detail FROM project p " +
                "LEFT JOIN project_technologies pt ON p.id = pt.project_id " +
                "LEFT JOIN project_details pd ON p.id = pd.project_id " +
                "WHERE p.id IN (SELECT projects_id FROM user_profile_projects WHERE user_profile_id = :id)";

        return jdbcTemplate.query(sql, new MapSqlParameterSource("id", userId), rs -> {
            Map<Long, ProjectDTO> map = new LinkedHashMap<>();
            while (rs.next()) {
                Long id = rs.getLong("id");
                ProjectDTO proj = map.getOrDefault(id, new ProjectDTO());
                if (proj.getTitle() == null) {
                    proj.setTitle(rs.getString("title"));
                    proj.setTechnologies(new ArrayList<>());
                    proj.setDetails(new ArrayList<>());
                    map.put(id, proj);
                }
                String tech = rs.getString("technology");
                String det = rs.getString("detail");
                if (tech != null && !proj.getTechnologies().contains(tech)) proj.getTechnologies().add(tech);
                if (det != null && !proj.getDetails().contains(det)) proj.getDetails().add(det);
            }
            return new ArrayList<>(map.values());
        });
    }

    // 5. Full Skills Breakdown
    private SkillsDTO fetchSkills(Long userId) {
        String sql = "SELECT fe.front_end, be.back_end, db.databases, dv.devops, ot.other " +
                "FROM SKILLS s " +
                "LEFT JOIN SKILLS_FRONT_END fe ON s.id = fe.skills_id " +
                "LEFT JOIN SKILLS_BACK_END be ON s.id = be.skills_id " +
                "LEFT JOIN SKILLS_DATABASES db ON s.id = db.skills_id " +
                "LEFT JOIN SKILLS_DEVOPS dv ON s.id = dv.skills_id " +
                "LEFT JOIN SKILLS_OTHER ot ON s.id = ot.skills_id " +
                "WHERE s.id = (SELECT SKILLS_ID FROM USER_PROFILE WHERE ID = :id)";

        return jdbcTemplate.query(sql, new MapSqlParameterSource("id", userId), rs -> {
            SkillsDTO s = new SkillsDTO();
            s.setFrontEnd(new ArrayList<>()); s.setBackEnd(new ArrayList<>());
            s.setDatabases(new ArrayList<>()); s.setDevops(new ArrayList<>()); s.setOther(new ArrayList<>());

            while (rs.next()) {
                addIfNotNull(s.getFrontEnd(), rs.getString("front_end"));
                addIfNotNull(s.getBackEnd(), rs.getString("back_end"));
                addIfNotNull(s.getDatabases(), rs.getString("databases"));
                addIfNotNull(s.getDevops(), rs.getString("devops"));
                addIfNotNull(s.getOther(), rs.getString("other"));
            }
            return s;
        });
    }

    private void addIfNotNull(List<String> list, String value) {
        if (value != null && !list.contains(value)) list.add(value);
    }
}