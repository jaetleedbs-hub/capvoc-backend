package buttle.apptpl.sup.bookmark.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import buttle.apptpl.sup.bookmark.dto.COM2070003Dto;
import buttle.apptpl.sup.bookmark.dto.COM2070008Dto;

@Mapper
public interface BookmarkMapper {

    /**
     * 즐겨찾기 카테고리 목록 조회
     * @param userId 사용자 ID
     * @return 카테고리 목록
     */
    List<COM2070003Dto> selectBookmarkCategoryList(@Param("userId") String userId);

    /**
     * 즐겨찾기 카테고리 최대 시퀀스 조회
     * @param userId 사용자 ID
     * @return 최대 시퀀스 번호
     */
    Integer selectMaxCategorySequence(@Param("userId") String userId);

    /**
     * 즐겨찾기 목록 조회
     * @param userId 사용자 ID
     * @return 즐겨찾기 목록
     */
    List<COM2070008Dto> selectBookmarkList(@Param("userId") String userId);
} 