package com.hcservice.controller;

import com.hcservice.annotation.UserLoginToken;
import com.hcservice.common.*;
import com.hcservice.common.utils.JwtUtil;
import com.hcservice.common.utils.StringUtil;
import com.hcservice.common.utils.TencentCOS;
import com.hcservice.domain.VO.UserVO;
import com.hcservice.domain.model.*;
import com.hcservice.common.BaseResult;
import com.hcservice.domain.request.BindHomeOwnerInfoRequest;
import com.hcservice.domain.request.ComplaintRequest;
import com.hcservice.domain.request.ReportRepairRequest;
import com.hcservice.domain.request.SubscribeRequest;
import com.hcservice.domain.response.*;
import com.hcservice.service.*;
import com.hcservice.web.interceptor.AuthenticationInterceptor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/hc")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private ComplaintService complaintService;

    @Autowired
    private CarService carService;

    @Autowired
    private HomeOwnerService homeOwnerService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private RepairService repairService;

    @Autowired
    private ServiceCostService serviceCostService;

    @Autowired
    private VisitorService visitorService;

    @Autowired
    private TencentCOS tencentCOS;

    @RequestMapping(value = "/user/getOtp",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_URLENCODED})
    public BaseResult getOtp(@RequestParam(name = "phoneNum") String phoneNum) {
        if(!StringUtil.verifyPhoneNum(phoneNum)) {
            return BaseResult.create(ErrorCode.PHONE_FORMAT_ERROR,"fail");
        }
        return userService.getOtp(phoneNum);
    }

    @RequestMapping(value = "/user/register",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_URLENCODED})
    public BaseResult register(@RequestParam(name = "phoneNum") String phoneNum,
                               @RequestParam(name = "otpCode") String otpCode,
                               @RequestParam(name = "password") String password) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        if(!StringUtil.verifyPhoneNum(phoneNum)) {
            return BaseResult.create(ErrorCode.PHONE_FORMAT_ERROR,"fail");
        }
        if(!userService.verityOtp(phoneNum, otpCode)) {//校验验证码
            return BaseResult.create(ErrorCode.OTP_CODE_ERROR,"fail");
        }
        if(!StringUtil.verifyPassword(password)) {
            return BaseResult.create(ErrorCode.PASSWORD_TOO_SIMPLE,"fail");
        }
        return userService.register(phoneNum, StringUtil.EncodeByMd5(password));
    }

    @RequestMapping(value = "/user/loginByOtp",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_URLENCODED})
    public BaseResult<String> loginByOtp(@RequestParam(name = "phoneNum") String phoneNum,
                                         @RequestParam(name = "otpCode") String otpCode) throws BusinessException {
        if(!StringUtil.verifyPhoneNum(phoneNum)) {
            return BaseResult.create(ErrorCode.PHONE_FORMAT_ERROR,"fail");
        }
        if(!userService.verityOtp(phoneNum, otpCode)) {//校验验证码
            return BaseResult.create(ErrorCode.OTP_CODE_ERROR,"fail");
        }
        User user = userService.loginByPhoneNum(phoneNum);
        String token = JwtUtil.getToken(user);
        return BaseResult.create(token);
    }

    @RequestMapping(value = "/user/loginByPwd",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_URLENCODED})
    public BaseResult<String> loginByPwd(@RequestParam(name = "phoneNum") String phoneNum,
                            @RequestParam(name = "password") String password) throws UnsupportedEncodingException, NoSuchAlgorithmException, BusinessException {
        if(!StringUtil.verifyPhoneNum(phoneNum)) {
            return BaseResult.create(ErrorCode.PHONE_FORMAT_ERROR,"fail");
        }
        User user = userService.loginByPwd(phoneNum, StringUtil.EncodeByMd5(password));
        String token = JwtUtil.getToken(user);
        return BaseResult.create(token);
    }

    @UserLoginToken
    @RequestMapping(value = "/user/getUserInfo",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_URLENCODED})
    public BaseResult<UserVO> getUserInfo() {
        User user = AuthenticationInterceptor.getLoginUser();
        UserVO userVO = convertUserVOFromUserModel(user);
        return BaseResult.create(userVO);
    }

    @UserLoginToken
    @RequestMapping(value = "/user/updateUserName",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_URLENCODED})
    public BaseResult<UserVO> updateUserName(@RequestParam("userName") String userName) {
        User user = AuthenticationInterceptor.getLoginUser();
        user.setUserName(userName);
        int res = userService.updateUser(user);
        if(res < 1) {
            return BaseResult.create(ErrorCode.UNKNOWN_ERROR,"fail");
        }
        UserVO userVO = convertUserVOFromUserModel(user);
        return BaseResult.create(userVO);
    }

    @UserLoginToken
    @RequestMapping(value = "/user/updateEmail",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_URLENCODED})
    public BaseResult<UserVO> updateEmail(@RequestParam("email") String email) {
        User user = AuthenticationInterceptor.getLoginUser();
        user.setEmail(email);
        int res = userService.updateUser(user);
        if(res < 1) {
            return BaseResult.create(ErrorCode.UNKNOWN_ERROR,"fail");
        }
        UserVO userVO = convertUserVOFromUserModel(user);
        return BaseResult.create(userVO);
    }

    @UserLoginToken
    @RequestMapping(value = "/user/updateHeadImage",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FROM_DATA})
    public BaseResult<UserVO> updateImage(@RequestParam("image") MultipartFile multipartfile)  {
        User user = AuthenticationInterceptor.getLoginUser();

        //获取文件的名称
        String fileName = multipartfile.getOriginalFilename();
        if (StringUtils.isBlank(fileName)) {
            return BaseResult.create(ErrorCode.IMAGE_CAN_NOT_BE_NULL, "fail");
        }
        //判断有无后缀
        if (fileName.lastIndexOf(".") < 0) {
            return BaseResult.create(ErrorCode.IMAGE_FORMAT_ERROR, "fail");
        }

        //获取文件后缀
        String prefix = fileName.substring(fileName.lastIndexOf("."));

        //如果不是图片
        if (!prefix.equalsIgnoreCase(".jpg") && !prefix.equalsIgnoreCase(".jpeg") && !prefix.equalsIgnoreCase(".svg") && !prefix.equalsIgnoreCase(".gif") && !prefix.equalsIgnoreCase(".png")) {
            return BaseResult.create(ErrorCode.IMAGE_FORMAT_ERROR, "fail");
        }
        final File excelFile;
        try {
            excelFile = File.createTempFile("imagesFile-" + System.currentTimeMillis(), prefix);
            //将Multifile转换成File
            multipartfile.transferTo(excelFile);
        } catch (IOException e) {
            return BaseResult.create(ErrorCode.UNKNOWN_ERROR, "fail");
        }

        //调用腾讯云工具上传文件
        String imageName = tencentCOS.uploadFile(excelFile, "headImage");

        //程序结束时，删除临时文件
        tencentCOS.deleteFile(excelFile);

        if (imageName == null) {
            return BaseResult.create(ErrorCode.IMAGE_UPDATE_ERROR, "fail");
        }
        user.setPictureUrl(imageName);
        int res = userService.updateUser(user);
        if(res < 1) {
            return BaseResult.create(ErrorCode.UNKNOWN_ERROR,"fail");
        }
        UserVO userVO = convertUserVOFromUserModel(user);
        return BaseResult.create(userVO);
    }


    /**
     * 用户查看小区公告接口
     * @return
     */
    @UserLoginToken
    @RequestMapping(value = "/user/getNotice",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_URLENCODED})
    public BaseResult<NoticeResponse> getNotice() {
        List<Notice> notices = noticeService.getAllNotice();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<NoticeResponse> responses = notices.stream().map(notice -> {
            NoticeResponse response = new NoticeResponse();
            BeanUtils.copyProperties(notice, response);
            response.setAdminName(notice.getAdmin().getAdminName());
            response.setCreateDate(dtf.format(notice.getCreateDate()));
            return response;
        }).collect(Collectors.toList());
        return BaseResult.create(responses);
    }

    /**
     * 用户服务投诉接口
     * @return
     */
    @UserLoginToken
    @RequestMapping(value = "/user/complain",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_URLENCODED})
    public BaseResult complain(ComplaintRequest request) {
        User user = AuthenticationInterceptor.getLoginUser();

        if(StringUtils.isEmpty(request.getType()) || StringUtils.isEmpty(request.getContent())) {
            return BaseResult.create(ErrorCode.PARAMETER_VALIDATION_ERROR);
        }

        Complaint complaint = new Complaint();
        BeanUtils.copyProperties(request, complaint);
        complaint.setCreateTime(LocalDateTime.now());
        complaint.setUser(user);
        int res = complaintService.addComplaint(complaint);
        if(res < 1) {
            return BaseResult.create(ErrorCode.UNKNOWN_ERROR,"fail");
        }
        return BaseResult.create(null);
    }

    @UserLoginToken
    @RequestMapping(value = "/user/addCarInfo", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_URLENCODED})
    public BaseResult addCarInfo(@RequestParam(name = "plateNum") String plateNum) {
        User user = AuthenticationInterceptor.getLoginUser();
        if (!StringUtil.verifyPlateNum(plateNum)) {
            return BaseResult.create(ErrorCode.PARAMETER_VALIDATION_ERROR, "fail");
        }
        Car car = new Car();
        car.setPlateNum(plateNum);
        car.setBeginTime(LocalDateTime.of(2000, 1, 1, 0, 0, 0));
        car.setEndTime(LocalDateTime.of(2000, 1, 1, 0, 0, 0));
        car.setType("未办理");
        car.setStatus(0);
        car.setUser(user);
        int res = carService.addCarInfo(car);
        if (res < 0) {
            return BaseResult.create(ErrorCode.PARAMETER_VALIDATION_ERROR, "fail");
        }
        return BaseResult.create(null);
    }

    @UserLoginToken
    @RequestMapping(value = "/user/getCarInfo", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_URLENCODED})
    public BaseResult<List<CarInfoResponse>> getCarInfo() {
        User user = AuthenticationInterceptor.getLoginUser();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<Car> carInfos = carService.getCarInfoByUserId(user.getUserId());
        List<CarInfoResponse> responses = carInfos.stream().map(carInfo -> {
            CarInfoResponse response = new CarInfoResponse();
            BeanUtils.copyProperties(carInfo, response);
            response.setBeginTime(dtf.format(carInfo.getBeginTime()));
            response.setEndTime(dtf.format(carInfo.getEndTime()));
            return response;
        }).collect(Collectors.toList());
        return BaseResult.create(responses);
    }

    @UserLoginToken
    @RequestMapping(value = "/user/bindHomeOwnerInfo", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_URLENCODED})
    public BaseResult bindHomeOwnerInfo(BindHomeOwnerInfoRequest request) throws BusinessException {
        User user = AuthenticationInterceptor.getLoginUser();
        if(StringUtils.isAnyEmpty(request.getName(), request.getCredentialType(), request.getCredentialNum(), request.getPhoneNum())) {
            return BaseResult.create(ErrorCode.PARAMETER_VALIDATION_ERROR, "fail");
        }
        int res = homeOwnerService.bindHomeOwnerInfo(request, user);
        if (res < 1) {
            return BaseResult.create(ErrorCode.UNKNOWN_ERROR, "fail");
        }
        return BaseResult.create(null);
    }

    @UserLoginToken
    @RequestMapping(value = "/user/getBindHouse", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_URLENCODED})
    public BaseResult<BindHouseInfoResponse> getBindHouse() {
        User user = AuthenticationInterceptor.getLoginUser();
        List<Room> rooms = homeOwnerService.getRoomsByUserId(user.getUserId());
        List<BindHouseInfoResponse> responses = rooms.stream().map(room -> {
            BindHouseInfoResponse response = new BindHouseInfoResponse();
            BeanUtils.copyProperties(room, response);
            response.setName(room.getOwner().getName());
            response.setCredentialType(room.getOwner().getCredentialType());
            response.setCredentialNum(room.getOwner().getCredentialNum());
            response.setPhoneNum(room.getOwner().getPhoneNum());
            return response;
        }).collect(Collectors.toList());
        return BaseResult.create(responses);
    }

    @UserLoginToken
    @RequestMapping(value = "/user/createCarChargeOrder", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_URLENCODED})
    public BaseResult<String> createCarChargeOrder(@RequestParam(name = "type")Integer type,
                                                   @RequestParam(name = "itemId")Integer itemId) throws BusinessException {
        User user = AuthenticationInterceptor.getLoginUser();
        String orderId = orderService.createCarChargeOrder(user, type, itemId);
        return BaseResult.create(orderId);
    }

    @UserLoginToken
    @RequestMapping(value = "/user/createServiceCostChargeOrder", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_URLENCODED})
    public BaseResult<String> createServiceCostChargeOrder(@RequestParam(name = "costId")Integer costId) throws BusinessException {
        User user = AuthenticationInterceptor.getLoginUser();
        String orderId = orderService.createServiceCostChargeOrder(user, costId);
        return BaseResult.create(orderId);
    }

    @UserLoginToken
    @RequestMapping(value = "/user/getOrderInfo", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_URLENCODED})
    public BaseResult<String> getOrderInfo(@RequestParam(name = "orderId")String orderId) {
        User user = AuthenticationInterceptor.getLoginUser();
        Order order = orderService.getOrderInfo(orderId);
        if(order == null) {
            return BaseResult.create(ErrorCode.ORDER_INFO_NOT_EXIST, "fail");
        }
        if (!order.getUser().getUserId().equals(user.getUserId())) {
            return BaseResult.create(ErrorCode.PARAMETER_VALIDATION_ERROR, "fail");
        }
        OrderInfoResponse response = new OrderInfoResponse();
        BeanUtils.copyProperties(order, response);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        response.setCreateTime(dtf.format(order.getCreateTime()));
        return BaseResult.create(response);
    }

    @UserLoginToken
    @RequestMapping(value = "/user/payOrder", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_URLENCODED})
    public BaseResult payOrder(@RequestParam(name = "orderId")String orderId) throws BusinessException {
        User user = AuthenticationInterceptor.getLoginUser();
        orderService.paymentOrder(orderId, user);
        return BaseResult.create(null);
    }

    @UserLoginToken
    @RequestMapping(value = "/user/getRepairRecord", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_URLENCODED})
    public BaseResult<List<RepairRecordResponse>> getRepairRecord() {
        User user = AuthenticationInterceptor.getLoginUser();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<Repair> repairs = repairService.getRepairRecordByUserId(user.getUserId());
        List<RepairRecordResponse> responses = repairs.stream().map(rp -> {
            RepairRecordResponse response = new RepairRecordResponse();
            BeanUtils.copyProperties(rp, response);
            response.setCreateTime(dtf.format(rp.getCreateTime()));
            if (rp.getAdmin() != null) {
                response.setAdminName(rp.getAdmin().getAdminName());
            }
            return response;
        }).collect(Collectors.toList());
        return BaseResult.create(responses);
    }

    @UserLoginToken
    @RequestMapping(value = "/user/reportRepair", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FROM_DATA})
    public BaseResult<List<RepairRecordResponse>> reportRepair(ReportRepairRequest request) {
        User user = AuthenticationInterceptor.getLoginUser();

        if (StringUtils.isEmpty(request.getType()) || StringUtils.isEmpty(request.getDescription())) {
            return BaseResult.create(ErrorCode.PARAMETER_VALIDATION_ERROR, "fail");
        }

        List<String> fileUrls = new ArrayList<>();
        for (MultipartFile multipartFile : request.getMultipartFiles()) {
            //获取文件的名称
            String fileName = multipartFile.getOriginalFilename();
            if (StringUtils.isBlank(fileName)) {
                return BaseResult.create(ErrorCode.IMAGE_CAN_NOT_BE_NULL, "fail");
            }
            //判断有无后缀
            if (fileName.lastIndexOf(".") < 0) {
                return BaseResult.create(ErrorCode.IMAGE_FORMAT_ERROR, "fail");
            }

            //获取文件后缀
            String prefix = fileName.substring(fileName.lastIndexOf("."));

            //如果不是图片
            if (!prefix.equalsIgnoreCase(".jpg") && !prefix.equalsIgnoreCase(".jpeg") && !prefix.equalsIgnoreCase(".svg") && !prefix.equalsIgnoreCase(".gif") && !prefix.equalsIgnoreCase(".png")) {
                return BaseResult.create(ErrorCode.IMAGE_FORMAT_ERROR, "fail");
            }
            final File excelFile;
            try {
                excelFile = File.createTempFile("imagesFile-" + System.currentTimeMillis(), prefix);
                //将Multifile转换成File
                multipartFile.transferTo(excelFile);
            } catch (IOException e) {
                return BaseResult.create(ErrorCode.UNKNOWN_ERROR, "fail");
            }

            //调用腾讯云工具上传文件
            String imageName = tencentCOS.uploadFile(excelFile, "repairImage");

            //程序结束时，删除临时文件
            tencentCOS.deleteFile(excelFile);
            if (imageName == null) {
                return BaseResult.create(ErrorCode.IMAGE_UPDATE_ERROR, "fail");
            }
            fileUrls.add(imageName);
        }
        StringBuilder imgUrl = new StringBuilder();
        if (fileUrls.size() > 0) {
            fileUrls.stream().forEach(url -> {
                imgUrl.append(url);
                imgUrl.append("|");
            });
            imgUrl.deleteCharAt(imgUrl.length() - 1);
        }
        Repair repair = new Repair();
        repair.setUser(user);
        repair.setDescription(request.getDescription());
        repair.setType(request.getType());
        repair.setCreateTime(LocalDateTime.now());
        repair.setImgUrl(imgUrl.toString());
        repair.setStatus(0);
        int res = repairService.reportRepair(repair);
        if (res < 1) {
            return BaseResult.create(ErrorCode.UNKNOWN_ERROR, "fail");
        }
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<Repair> repairs = repairService.getRepairRecordByUserId(user.getUserId());
        List<RepairRecordResponse> responses = repairs.stream().map(rp -> {
            RepairRecordResponse response = new RepairRecordResponse();
            BeanUtils.copyProperties(rp, response);
            response.setCreateTime(dtf.format(rp.getCreateTime()));
            if (rp.getAdmin() != null) {
                response.setAdminName(rp.getAdmin().getAdminName());
            }
            return response;
        }).collect(Collectors.toList());
        return BaseResult.create(responses);
    }

    @UserLoginToken
    @RequestMapping(value = "/user/getServiceCostRecord", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_URLENCODED})
    public BaseResult<ServiceCostRecordResponse> getServiceCostRecord() {
        User user = AuthenticationInterceptor.getLoginUser();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<ServiceCost> costList = serviceCostService.getNotPayServiceCostRecord(user.getUserId());
        List<ServiceCostRecordResponse> responses = costList.stream().map(cost -> {
            ServiceCostRecordResponse response = new ServiceCostRecordResponse();
            BeanUtils.copyProperties(cost, response);
            response.setTime(dtf.format(cost.getTime()));
            response.setBuildingNum(cost.getRoom().getBuildingNum());
            response.setRoomNum(cost.getRoom().getRoomNum());
            return response;
        }).collect(Collectors.toList());
        return BaseResult.create(responses);
    }

    @UserLoginToken
    @RequestMapping(value = "/user/getRecord",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_URLENCODED})
    public BaseResult<List<VisitRecordResponse>> getVisitRecord() {
        User user = AuthenticationInterceptor.getLoginUser();
        List<Visitor> visitorRecords = visitorService.getVisitsByUserId(user.getUserId());
        List<VisitRecordResponse> responses = visitorRecords.stream().map(record -> {
            VisitRecordResponse response = new VisitRecordResponse();
            BeanUtils.copyProperties(record, response);
            response.setRoomNum(record.getRoom().getRoomNum());
            response.setBuildingNum(record.getRoom().getBuildingNum());
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            response.setCreateTime(dtf.format(record.getCreateTime()));
            response.setVisitTime(dtf.format(record.getVisitTime()));
            return response;
        }).collect(Collectors.toList());
        return BaseResult.create(responses);
    }

    @UserLoginToken
    @RequestMapping(value = "/user/subscribe",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_URLENCODED})
    public BaseResult subscribe(SubscribeRequest request) throws BusinessException {
        User user = AuthenticationInterceptor.getLoginUser();
        int res = visitorService.addVisitRecord(request, user);
        if(res < 1) {
            return BaseResult.create(ErrorCode.UNKNOWN_ERROR,"fail");
        }
        return BaseResult.create(null);
    }

    private UserVO convertUserVOFromUserModel(User user) {
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }


}
