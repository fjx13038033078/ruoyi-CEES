package com.ruoyi.college.service.impl;

import com.ruoyi.college.domain.VolunteerForm;
import com.ruoyi.college.domain.VolunteerItem;
import com.ruoyi.college.mapper.VolunteerFormMapper;
import com.ruoyi.college.mapper.VolunteerItemMapper;
import com.ruoyi.college.service.IVolunteerFormService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class VolunteerFormServiceImpl implements IVolunteerFormService {

    private final VolunteerFormMapper volunteerFormMapper;
    private final VolunteerItemMapper volunteerItemMapper;

    @Override
    public List<VolunteerForm> selectFormListByUserId(Long userId) {
        return volunteerFormMapper.selectFormListByUserId(userId);
    }

    @Override
    public VolunteerForm selectByFormId(Long formId) {
        VolunteerForm form = volunteerFormMapper.selectByFormId(formId);
        if (form != null) {
            form.setItems(volunteerItemMapper.selectItemsByFormId(formId));
        }
        return form;
    }

    @Override
    public int createForm(VolunteerForm form) {
        if (form.getStatus() == null) {
            form.setStatus(0);
        }
        if (form.getTotalItems() == null) {
            form.setTotalItems(0);
        }
        if (form.getWarningCount() == null) {
            form.setWarningCount(0);
        }
        return volunteerFormMapper.insertForm(form);
    }

    @Override
    public int updateForm(VolunteerForm form) {
        return volunteerFormMapper.updateForm(form);
    }

    @Override
    @Transactional
    public int deleteForm(Long formId) {
        volunteerItemMapper.deleteByFormId(formId);
        return volunteerFormMapper.deleteByFormId(formId);
    }

    @Override
    public List<VolunteerItem> selectItemsByFormId(Long formId) {
        return volunteerItemMapper.selectItemsByFormId(formId);
    }

    @Override
    public int addItem(VolunteerItem item) {
        int result = volunteerItemMapper.insertItem(item);
        refreshFormTotalItems(item.getFormId());
        return result;
    }

    @Override
    public int updateItem(VolunteerItem item) {
        return volunteerItemMapper.updateItem(item);
    }

    @Override
    public int deleteItem(Long itemId) {
        VolunteerItem item = volunteerItemMapper.selectByItemId(itemId);
        int result = volunteerItemMapper.deleteByItemId(itemId);
        if (item != null) {
            refreshFormTotalItems(item.getFormId());
        }
        return result;
    }

    @Override
    @Transactional
    public int updateItemSortOrders(List<VolunteerItem> items) {
        int count = 0;
        for (VolunteerItem item : items) {
            VolunteerItem update = new VolunteerItem();
            update.setItemId(item.getItemId());
            update.setSortOrder(item.getSortOrder());
            count += volunteerItemMapper.updateItem(update);
        }
        return count;
    }

    private void refreshFormTotalItems(Long formId) {
        int count = volunteerItemMapper.countByFormId(formId);
        VolunteerForm update = new VolunteerForm();
        update.setFormId(formId);
        update.setTotalItems(count);
        volunteerFormMapper.updateForm(update);
    }
}
