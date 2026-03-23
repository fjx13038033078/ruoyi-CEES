package com.ruoyi.college.mapper;

import com.ruoyi.college.domain.VolunteerItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface VolunteerItemMapper {

    List<VolunteerItem> selectItemsByFormId(Long formId);

    VolunteerItem selectByItemId(Long itemId);

    int insertItem(VolunteerItem item);

    int updateItem(VolunteerItem item);

    int updateCheckResult(@Param("itemId") Long itemId, @Param("checkStatus") Integer checkStatus,
                          @Param("checkMessages") String checkMessages);

    int deleteByItemId(Long itemId);

    int deleteByFormId(Long formId);

    int countByFormId(Long formId);
}
