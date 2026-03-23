package com.ruoyi.web.controller.college;

import com.ruoyi.college.domain.VolunteerForm;
import com.ruoyi.college.domain.VolunteerItem;
import com.ruoyi.college.domain.dto.VolunteerCheckResultDTO;
import com.ruoyi.college.service.IVolunteerCheckService;
import com.ruoyi.college.service.IVolunteerFormService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/college/volunteer")
public class VolunteerController extends BaseController {

    private final IVolunteerFormService volunteerFormService;
    private final IVolunteerCheckService volunteerCheckService;

    @GetMapping("/formList")
    public AjaxResult formList() {
        Long userId = SecurityUtils.getUserId();
        List<VolunteerForm> list = volunteerFormService.selectFormListByUserId(userId);
        return success(list);
    }

    @GetMapping("/form/{formId}")
    public AjaxResult getForm(@PathVariable Long formId) {
        return success(volunteerFormService.selectByFormId(formId));
    }

    @PostMapping("/form/add")
    public AjaxResult addForm(@RequestBody VolunteerForm form) {
        form.setUserId(SecurityUtils.getUserId());
        volunteerFormService.createForm(form);
        return success(form);
    }

    @PutMapping("/form/update")
    public AjaxResult updateForm(@RequestBody VolunteerForm form) {
        return toAjax(volunteerFormService.updateForm(form));
    }

    @DeleteMapping("/form/delete/{formId}")
    public AjaxResult deleteForm(@PathVariable Long formId) {
        return toAjax(volunteerFormService.deleteForm(formId));
    }

    @GetMapping("/items/{formId}")
    public AjaxResult getItems(@PathVariable Long formId) {
        return success(volunteerFormService.selectItemsByFormId(formId));
    }

    @PostMapping("/item/add")
    public AjaxResult addItem(@RequestBody VolunteerItem item) {
        volunteerFormService.addItem(item);
        return success(item);
    }

    @PutMapping("/item/update")
    public AjaxResult updateItem(@RequestBody VolunteerItem item) {
        return toAjax(volunteerFormService.updateItem(item));
    }

    @DeleteMapping("/item/delete/{itemId}")
    public AjaxResult deleteItem(@PathVariable Long itemId) {
        return toAjax(volunteerFormService.deleteItem(itemId));
    }

    @PutMapping("/item/sort")
    public AjaxResult updateSort(@RequestBody List<VolunteerItem> items) {
        return toAjax(volunteerFormService.updateItemSortOrders(items));
    }

    @GetMapping("/check/{formId}")
    public AjaxResult checkForm(@PathVariable Long formId) {
        List<VolunteerCheckResultDTO> results = volunteerCheckService.checkVolunteerForm(formId);
        return success(results);
    }
}
