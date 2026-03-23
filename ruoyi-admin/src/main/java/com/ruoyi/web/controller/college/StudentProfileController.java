package com.ruoyi.web.controller.college;

import com.ruoyi.college.domain.StudentProfile;
import com.ruoyi.college.service.IStudentProfileService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/college/student")
public class StudentProfileController extends BaseController {

    private final IStudentProfileService studentProfileService;

    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermi('college:student:verify')")
    public TableDataInfo list(StudentProfile query) {
        startPage();
        List<StudentProfile> list = studentProfileService.selectProfileList(query);
        return getDataTable(list);
    }

    @GetMapping("/myProfile")
    public AjaxResult myProfile() {
        Long userId = SecurityUtils.getUserId();
        StudentProfile profile = studentProfileService.selectByUserId(userId);
        return success(profile);
    }

    @GetMapping("/detail/{profileId}")
    public AjaxResult detail(@PathVariable Long profileId) {
        return success(studentProfileService.selectByProfileId(profileId));
    }

    @PostMapping("/save")
    public AjaxResult save(@RequestBody StudentProfile profile) {
        Long userId = SecurityUtils.getUserId();
        StudentProfile existing = studentProfileService.selectByUserId(userId);
        if (existing != null) {
            profile.setProfileId(existing.getProfileId());
            profile.setUserId(userId);
            return toAjax(studentProfileService.updateProfile(profile));
        } else {
            profile.setUserId(userId);
            profile.setVerifyStatus(0);
            return toAjax(studentProfileService.createProfile(profile));
        }
    }

    @PostMapping("/add")
    public AjaxResult add(@RequestBody StudentProfile profile) {
        profile.setUserId(SecurityUtils.getUserId());
        return toAjax(studentProfileService.createProfile(profile));
    }

    @PutMapping("/update")
    public AjaxResult update(@RequestBody StudentProfile profile) {
        return toAjax(studentProfileService.updateProfile(profile));
    }

    @PutMapping("/verify/{profileId}/{verifyStatus}")
    @PreAuthorize("@ss.hasPermi('college:student:verify')")
    public AjaxResult verify(@PathVariable Long profileId, @PathVariable Integer verifyStatus) {
        return toAjax(studentProfileService.updateVerifyStatus(profileId, verifyStatus));
    }

    @DeleteMapping("/delete/{profileId}")
    @PreAuthorize("@ss.hasPermi('college:student:verify')")
    public AjaxResult delete(@PathVariable Long profileId) {
        return toAjax(studentProfileService.deleteByProfileId(profileId));
    }
}
