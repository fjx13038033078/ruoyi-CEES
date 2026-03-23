package com.ruoyi.college.service;

import com.ruoyi.college.domain.VolunteerForm;
import com.ruoyi.college.domain.VolunteerItem;

import java.util.List;

public interface IVolunteerFormService {

    List<VolunteerForm> selectFormListByUserId(Long userId);

    VolunteerForm selectByFormId(Long formId);

    int createForm(VolunteerForm form);

    int updateForm(VolunteerForm form);

    int deleteForm(Long formId);

    List<VolunteerItem> selectItemsByFormId(Long formId);

    int addItem(VolunteerItem item);

    int updateItem(VolunteerItem item);

    int deleteItem(Long itemId);

    int updateItemSortOrders(List<VolunteerItem> items);
}
