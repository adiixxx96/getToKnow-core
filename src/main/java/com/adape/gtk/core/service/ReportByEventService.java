package com.adape.gtk.core.service;

import java.util.List;

import com.adape.gtk.core.client.beans.ReportByEventDTO;
import com.adape.gtk.core.dao.entity.ReportByEvent;
import com.adape.gtk.core.utils.TreeNode;

public interface ReportByEventService extends CRUDService<ReportByEventDTO, Integer>{
	
	ReportByEvent parseReportByEvent(ReportByEventDTO reportByEvent);
	
	ReportByEventDTO parseReportByEvent(ReportByEvent reportByEvent, List<TreeNode<String>> params);

}