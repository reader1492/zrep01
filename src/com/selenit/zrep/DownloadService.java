// Valerii Zinovev, Perm, 10 aug 2017
// ������������ ������ ��� �������� ������
package com.selenit.zrep;

import java.io.File;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

@Path("/download")
public class DownloadService {
	
	// ����� ������ ����������
	public final static String VERSION = "v1.08-171215";
	
	@GET
	@Path("/{report}")
	//@Produces(MediaType.APPLICATION_XML+ ";charset=utf-8")
	public Response getRestAnswerFile(
			@PathParam("report") String rep,					// ��� ������
			@QueryParam("pers_request_id") String pers_req) {	// ���� ��� ������
		
		restAnswer rest = new restAnswer();		// �������������� ���������
		File fileToSend = null;					// ����� ��� ������������ �����
		String repName = Report.rep1_output;	// �������� ������
		
		try {
			switch (rep) {
			case "rep1":	// ������ ����� (���-����)
				repName = Report.rep1_output;
				rest = Report.doReport1 (pers_req); break;
			case "rep2":	// ������ ����� (������ �� ��������� ����������� ��������)
				repName = Report.rep2_output;
				rest = Report.doReport2 (pers_req); break;
			case "rep3":	// ������ ����� (������ �������)
				repName = Report.rep3_output;
				rest = Report.doReport3 (pers_req); break;
			case "rep4":	// ��������� ����� (������ �������)
				repName = Report.rep4_output;
				rest = Report.doReport4 (pers_req); break;
			case "rep5":	// ����� ����� (����� ����� �� �������)
				repName = Report.rep5_output;
				rest = Report.doReport5 (pers_req); break;
			case "rrr":	// ����
				rest = Report.doRRR(pers_req);
				break;
			case "ver": // ������
		    	// ������������ ��������� ����������
		    	rest.setStatus("success");
		    	rest.setCode(200);
		    	rest.setMessage(VERSION);
		    	rest.setData(VERSION);
				return Response.ok().entity(VERSION).build();
			default:	// ������������ �����
				rest.setStatus("error");
				rest.setCode(602);
				rest.setMessage("Invalid report name \'" + rep + "\'");
				rest.setData("Invalid report name \'" + rep + "\'");
			}
			if (rest.getCode() == 200) fileToSend = new File(rest.getData());
		} catch (Exception e) {
			// ������������ JSON-��������� �� ������
			e.printStackTrace();
			rest.setStatus("error");
			rest.setCode(500);
			rest.setMessage("Internal Server Error");
			rest.setData("���������� ������ �������");
		}
		// ������������ ������
		if (rest.getCode() == 200)
		{
			// � ������ ������ - ��������� �����
			ResponseBuilder response = Response.ok((Object)fileToSend);
			response.header("Content-Disposition", "attachment; filename=" + repName);
			return response.build();
			//return Response.ok(fileToSend, "application/zip").build();
		}	// � ������ ������� - ��������� �� ������
		return Response.serverError().entity(rest.getData()).build();
	}

}
