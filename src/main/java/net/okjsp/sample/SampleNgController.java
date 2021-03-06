package net.okjsp.sample;

import net.okjsp.common.model.Paging;
import net.okjsp.common.model.PagingList;
import net.okjsp.common.model.Result;
import net.okjsp.layout.BasicLayoutController;
import net.okjsp.sample.model.Sample;
import net.okjsp.sample.service.SampleBoardService;
import net.okjsp.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * User: langerhans
 * Date: 2013. 10. 30.
 * Time: 오전 1:30
 * Description : Sample Controller for AngularJS
 */

@Controller
@RequestMapping(value="/sample_ng")
public class SampleNgController extends BasicLayoutController {

    @Autowired
    SampleBoardService sampleBoardService;

    /**
     * 기본 Layout Load
     *
     * AngularJS 의 $routeProvider 의 html5 mode 사용시 Reload / 외부진입을 위해서 when에 사용되는 모든 url을
     * RequestMapping 의 value 에 등록해준다.
     *
     * @return
     */
    @RequestMapping(value = {"/{categoryId}", "/{categoryId}/view/{id}", "/{categoryId}/modify/{id}", "/{categoryId}/create"}, method = RequestMethod.GET)
    public String main() {
        return "sample_ng/sample_ng";
    }

    /**
     * Sample Data 목록 JSON
     * @param categoryId
     * @param paging
     * @return
     */
    @RequestMapping(value = "/{categoryId}.json", method = RequestMethod.GET)
    public @ResponseBody PagingList listAsJson(
            @PathVariable int categoryId,
            Paging paging) {

        List<Sample> list = sampleBoardService.getList(categoryId, paging);

        int count = sampleBoardService.getTotalCount(categoryId);

        paging.setListCount(list.size());
        paging.setTotalCount(count);

        PagingList pagingList = new PagingList(paging, list);

        return pagingList;
    }

    /**
     * Sample Data 상세보기 JSON
     * @param id
     * @return
     */
    @RequestMapping(value = "/{categoryId}/{id}.json", method = RequestMethod.GET)
    public @ResponseBody Sample viewAsJson(
            @PathVariable int id) {

        //조회수 증가
        sampleBoardService.addViewCount(id);

        Sample sampleBoard = sampleBoardService.getOne(id);

        return sampleBoard;
    }

    /**
     * Sample Data JSON 으로 생성
     * @param categoryId
     * @param sample
     * @return
     */
    @Secured("ROLE_USER")
    @RequestMapping(value = "/{categoryId}", method = RequestMethod.POST)
    public @ResponseBody Result createByJson(
            @PathVariable int categoryId,
            @RequestBody Sample sample,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        sample.setCategoryId(categoryId);
        sample.setWriteId(user.getUserId());

        boolean isCreated = sampleBoardService.create(sample);

        Result result = new Result(isCreated, "저장이 완료되었습니다.");

        return result;
    }

    /**
     * Sample Data JSON 으로 수정
     * @param sample
     * @return
     */
    @RequestMapping(value = "/{categoryId}/{id}.json", method = RequestMethod.POST)
    public @ResponseBody Result updateByJson(
            @RequestBody Sample sample) {

        boolean isCreated = sampleBoardService.modify(sample);

        Result result = new Result(isCreated, "수정이 완료되었습니다.");

        return result;
    }

    /**
     * Sample Data 삭제
     * @param id
     * @return
     */
    @RequestMapping(value = "/{categoryId}/{id}.json", method = RequestMethod.DELETE)
    public @ResponseBody Result removeByJson(
            @PathVariable int id) {

        boolean isCreated = sampleBoardService.destroy(id);

        Result result = new Result(isCreated, "삭제가 완료되었습니다.");

        return result;
    }

}
