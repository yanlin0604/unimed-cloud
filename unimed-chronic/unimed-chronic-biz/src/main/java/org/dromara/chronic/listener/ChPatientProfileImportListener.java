package org.dromara.chronic.listener;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HtmlUtil;
import cn.idev.excel.context.AnalysisContext;
import cn.idev.excel.event.AnalysisEventListener;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.dromara.chronic.domain.bo.ChPatientProfileBo;
import org.dromara.chronic.domain.vo.ChPatientProfileImportVo;
import org.dromara.chronic.manager.PatientProfileManager;
import org.dromara.chronic.service.IChPatientProfileService;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.SpringUtils;
import org.dromara.common.core.utils.StreamUtils;
import org.dromara.common.core.utils.ValidatorUtils;
import org.dromara.common.excel.core.ExcelListener;
import org.dromara.common.excel.core.ExcelResult;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.system.api.RemoteDeptService;
import org.dromara.system.api.RemoteUserService;
import org.dromara.system.api.domain.vo.RemoteDeptVo;
import org.dromara.system.api.model.LoginUser;
import org.dromara.common.core.utils.StringUtils;

import java.util.Collections;
import java.util.List;

/**
 * 患者档案自定义导入
 *
 * @author unimed
 */
@Slf4j
public class ChPatientProfileImportListener extends AnalysisEventListener<ChPatientProfileImportVo> implements ExcelListener<ChPatientProfileImportVo> {

    private final IChPatientProfileService patientProfileService;
    private final PatientProfileManager patientProfileManager;
    private final RemoteDeptService remoteDeptService;
    private final RemoteUserService remoteUserService;
    private final Boolean isUpdateSupport;
    private final Long operUserId;

    private int successNum = 0;
    private int failureNum = 0;
    private final StringBuilder successMsg = new StringBuilder();
    private final StringBuilder failureMsg = new StringBuilder();

    public ChPatientProfileImportListener(Boolean isUpdateSupport) {
        this.patientProfileService = SpringUtils.getBean(IChPatientProfileService.class);
        this.patientProfileManager = SpringUtils.getBean(PatientProfileManager.class);
        this.remoteDeptService = SpringUtils.getBean(RemoteDeptService.class);
        this.remoteUserService = SpringUtils.getBean(RemoteUserService.class);
        this.isUpdateSupport = isUpdateSupport;
        this.operUserId = LoginHelper.getUserId();
    }

    @Override
    public void invoke(ChPatientProfileImportVo patientVo, AnalysisContext context) {
        try {
            ChPatientProfileBo bo = BeanUtil.toBean(patientVo, ChPatientProfileBo.class);
            
            // 处理科室名称到ID的转换
            if (StringUtils.isNotBlank(patientVo.getDeptName())) {
                List<RemoteDeptVo> deptVos = remoteDeptService.selectDeptsByList();
                for (RemoteDeptVo deptVo : deptVos) {
                    if (patientVo.getDeptName().equals(deptVo.getDeptName())) {
                        bo.setDeptId(deptVo.getDeptId());
                        break;
                    }
                }
            }
            
            // 处理医生名称到ID的转换
            if (StringUtils.isNotBlank(patientVo.getDoctorUserName())) {
                try {
                    LoginUser doctor = remoteUserService.getUserInfo(patientVo.getDoctorUserName(), LoginHelper.getTenantId());
                    if (doctor != null) {
                        bo.setDoctorUserId(doctor.getUserId());
                    }
                } catch (Exception e) {
                    log.warn("导入时查询责任医生失败: {}", patientVo.getDoctorUserName());
                }
            }

            ValidatorUtils.validate(bo);
            
            if (ObjectUtil.isNull(bo.getSource())) {
                bo.setSource("MANUAL");
            }
            if (ObjectUtil.isNull(bo.getManageStatus())) {
                bo.setManageStatus("PENDING_ENTRY");
            }
            
            patientProfileManager.createArchive(bo, Collections.emptyList(), Collections.emptyList());
            successNum++;
            successMsg.append("<br/>").append(successNum).append("、患者 ").append(bo.getName()).append(" 导入成功");
        } catch (Exception e) {
            failureNum++;
            String msg = "<br/>" + failureNum + "、患者 " + HtmlUtil.cleanHtmlTag(patientVo.getName()) + " 导入失败：";
            String message = e.getMessage();
            if (e instanceof ConstraintViolationException cvException) {
                message = StreamUtils.join(cvException.getConstraintViolations(), ConstraintViolation::getMessage, ", ");
            }
            failureMsg.append(msg).append(message);
            log.error(msg, e);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {

    }

    @Override
    public ExcelResult<ChPatientProfileImportVo> getExcelResult() {
        return new ExcelResult<ChPatientProfileImportVo>() {
            @Override
            public String getAnalysis() {
                if (failureNum > 0) {
                    failureMsg.insert(0, "很抱歉，导入失败！共 " + failureNum + " 条数据格式不正确，错误如下：");
                    throw new ServiceException(failureMsg.toString());
                } else {
                    successMsg.insert(0, "恭喜您，数据已全部导入成功！共 " + successNum + " 条，数据如下：");
                }
                return successMsg.toString();
            }

            @Override
            public List<ChPatientProfileImportVo> getList() { return null; }

            @Override
            public List<String> getErrorList() { return null; }
        };
    }
}
