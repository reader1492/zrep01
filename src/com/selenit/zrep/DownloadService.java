// Valerii Zinovev, Perm, 10 aug 2017
// Формирование ответа для загрузки отчета
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
	
	// Номер версии приложения
	public final static String VERSION = "v1.08-171215";
	
	@GET
	@Path("/{report}")
	//@Produces(MediaType.APPLICATION_XML+ ";charset=utf-8")
	public Response getRestAnswerFile(
			@PathParam("report") String rep,					// Имя отчета
			@QueryParam("pers_request_id") String pers_req) {	// Ключ для отчета
		
		restAnswer rest = new restAnswer();		// Результирующая структура
		File fileToSend = null;					// Поток для принимаемого файла
		String repName = Report.rep1_output;	// Название отчета
		
		try {
			switch (rep) {
			case "rep1":	// Первый отчет (Чек-лист)
				repName = Report.rep1_output;
				rest = Report.doReport1 (pers_req); break;
			case "rep2":	// Второй отчет (Заявка на получение независимой гарантии)
				repName = Report.rep2_output;
				rest = Report.doReport2 (pers_req); break;
			case "rep3":	// Третий отчет (Резюме проекта)
				repName = Report.rep3_output;
				rest = Report.doReport3 (pers_req); break;
			case "rep4":	// Четвертый отчет (Анкета проекта)
				repName = Report.rep4_output;
				rest = Report.doReport4 (pers_req); break;
			case "rep5":	// Пятый отчет (Общий отчет по заявкам)
				repName = Report.rep5_output;
				rest = Report.doReport5 (pers_req); break;
			case "rrr":	// Пути
				rest = Report.doRRR(pers_req);
				break;
			case "ver": // Версия
		    	// Формирование успешного результата
		    	rest.setStatus("success");
		    	rest.setCode(200);
		    	rest.setMessage(VERSION);
		    	rest.setData(VERSION);
				return Response.ok().entity(VERSION).build();
			default:	// Неопознанный отчет
				rest.setStatus("error");
				rest.setCode(602);
				rest.setMessage("Invalid report name \'" + rep + "\'");
				rest.setData("Invalid report name \'" + rep + "\'");
			}
			if (rest.getCode() == 200) fileToSend = new File(rest.getData());
		} catch (Exception e) {
			// Сформировать JSON-сообщение об ошибке
			e.printStackTrace();
			rest.setStatus("error");
			rest.setCode(500);
			rest.setMessage("Internal Server Error");
			rest.setData("Внутренняя ошибка сервера");
		}
		// Формирование ответа
		if (rest.getCode() == 200)
		{
			// В случае успеха - пересылка файла
			ResponseBuilder response = Response.ok((Object)fileToSend);
			response.header("Content-Disposition", "attachment; filename=" + repName);
			return response.build();
			//return Response.ok(fileToSend, "application/zip").build();
		}	// В случае неудачи - сообщение об ошибке
		return Response.serverError().entity(rest.getData()).build();
	}

}
