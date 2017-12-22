// Valerii Zinovev, Perm, 08 aug 2017
// Формирование отчетов
package com.selenit.zrep;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.nio.file.attribute.PosixFilePermission;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.mongodb.BasicDBList;

//import org.apache.commons.lang3.StringUtils;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class Report {
	// Константы
	// Пути
	public static final String templates_dir = "/templates/";
	public static final String template_Path = "/var/lib/tomcat8/webapps/template/";	
	public static final String result_Path = "/var/lib/tomcat8/webapps/result/";	
	public static final String rep1_template = "r1_template.docx";
	public static final String rep2_template = "r2_template.docx";
	public static final String rep3_template = "r3_template.docx";
	public static final String rep4_template = "r4_template.xlsx";
	public static final String rep5_template = "r5_template.xlsx";
	public static final String rep1_output = "r1_checkList.docx";
	public static final String rep2_output = "r2_quest.docx";
	public static final String rep3_output = "r3_resume.docx";
	public static final String rep4_output = "r4_application.xlsx";
	public static final String rep5_output = "r5_report.xlsx";
	public static final String rep1_prefix = "r1_";
	public static final String rep2_prefix = "r2_";
	public static final String rep3_prefix = "r3_";
	public static final String rep4_prefix = "r4_";
	public static final String rep5_prefix = "r5_";
	public static final String rep1_suffix = ".docx";
	public static final String rep2_suffix = ".docx";
	public static final String rep3_suffix = ".docx";
	public static final String rep4_suffix = ".xlsx";
	public static final String rep5_suffix = ".xlsx";
	
	// БД
	public static final String rep_bd_server = "localhost";
	public static final int rep_bd_port = 27017;
	public static final String rep_bd_name = "first";
	// Приложение
	public static final String parent_name = "pers_request_id";	// Ключ
	public static final int benif_percent = 25;					// Процент бенифициара
	public static final String question_marks = "??????";		// Знаки вопроса
	// ОС
	private static final boolean isPosix =
		    FileSystems.getDefault().supportedFileAttributeViews().contains("posix");
	
	// Отлладка rrr
	public static restAnswer doRRR(String aParam) {
		restAnswer rest = new restAnswer();	// Результат
		String x, y, z, s;
		x = System.getProperty("catalina.base");
		y = System.getProperty("catalina.home");
		z = System.getProperty("java.io.tmpdir");
		s = "catalina.base = \"" + x + 
				"\", catalina.home = \"" + y + 
				"\", java.io.tmpdir = \"" + z;
		
		rest.setStatus("error");
		rest.setCode(666);
		rest.setMessage("doRRR");
		rest.setData(s);
		
		return rest;
	}

	// Отчет #1 (Чек-лист)
	public static restAnswer doReport1(String aParam) {
		restAnswer rest = new restAnswer();	// Результат
		List<String> body = new ArrayList<String>();
		try {
			// Скопировать шаблон в результирующий файл
			String result_name = new String(System.getProperty("java.io.tmpdir") + "/" +
					Report.rep1_prefix + Report.getUUID() + Report.rep1_suffix);
			Files.copy(Paths.get(
					System.getProperty("catalina.base") + Report.templates_dir + rep1_template), 
					Paths.get(result_name), 
					StandardCopyOption.REPLACE_EXISTING);
			// Установка атрибутов результирующего файла (для UNIX)
			if (isPosix) {
				Set<PosixFilePermission> perms = new HashSet<PosixFilePermission>();
				//add owners permission
				perms.add(PosixFilePermission.OWNER_READ);
				perms.add(PosixFilePermission.OWNER_WRITE);
				//perms.add(PosixFilePermission.OWNER_EXECUTE);
				//add group permissions
				perms.add(PosixFilePermission.GROUP_READ);
				perms.add(PosixFilePermission.GROUP_WRITE);
				//perms.add(PosixFilePermission.GROUP_EXECUTE);
				//add others permissions
				perms.add(PosixFilePermission.OTHERS_READ);
				perms.add(PosixFilePermission.OTHERS_WRITE);
				//perms.add(PosixFilePermission.OTHERS_EXECUTE);
	        
				Files.setPosixFilePermissions(Paths.get(result_name), perms);
			}
			// Скопировать /word/document.xml в буфер
		    try (FileSystem zipFileSys = createZipFileSystem(result_name, false)) {
		        Path path = zipFileSys.getPath("/word/document.xml");
		        body = Files.readAllLines(path);
		    }
		    // Корректировка body
		    List<String> res = Rep1.build(body, aParam);
		    if (res == null) {
				// Формирование отчета об ошибке
			    rest.setStatus("error");
			    rest.setCode(610);
			    rest.setMessage("The report \'Check list\' is failure (DB error)");
			    rest.setData("The report \'Check list\' is failure (DB error)");
		    } else if (res.get(0).equals("error")) {
				// Формирование отчета об ошибке
			    rest.setStatus("error");
			    rest.setCode(610);
			    rest.setMessage("The report \'Check list\' is failure (DB error)");
			    rest.setData(res.get(1));
		    } else {
		    	// Запись body в файл
		    	try (FileSystem zipFileSys = createZipFileSystem(result_name, false)) {
		    		Path path = zipFileSys.getPath("/word/document.xml");
		    		Files.write(path, body, Charset.forName("UTF-8"), StandardOpenOption.CREATE);
		    	}
		    	// Формирование успешного результата
		    	rest.setStatus("success");
		    	rest.setCode(200);
		    	rest.setMessage("OK");
		    	rest.setData(result_name);
		    }
		} catch(IOException  ex) {
			ex.printStackTrace();
			System.out.println("REP: " + ex.toString());
			// Формирование отчета об ошибке
		    rest.setStatus("error");
		    rest.setCode(602);
		    rest.setMessage("The report \'Check list\' is failure (IO-error)");
		    rest.setData(ex.toString());
		} catch(Exception  ex) {
			ex.printStackTrace();
			System.out.println("REPxx: " + ex.toString());
			// Формирование отчета об ошибке
		    rest.setStatus("error");
		    rest.setCode(610);
		    rest.setMessage("The report \'Check list\' is failure (DB error)");
		    rest.setData(ex.toString());
		}
		return rest;
	}

	// Отчет #2 (Заявка на получение независимой гарантии)
	public static restAnswer doReport2(String aParam) {
		restAnswer rest = new restAnswer();	// Результат
		List<String> body = new ArrayList<String>();
		try {
			// Скопировать шаблон в результирующий файл
			String result_name = new String(System.getProperty("java.io.tmpdir") + "/" +
					Report.rep2_prefix + Report.getUUID() + Report.rep2_suffix);
			Files.copy(Paths.get(
					System.getProperty("catalina.base") + Report.templates_dir + rep2_template), 
					Paths.get(result_name), 
					StandardCopyOption.REPLACE_EXISTING);
			// Установка атрибутов результирующего файла (для UNIX)
			if (isPosix) {
				Set<PosixFilePermission> perms = new HashSet<PosixFilePermission>();
				//add owners permission
				perms.add(PosixFilePermission.OWNER_READ);
				perms.add(PosixFilePermission.OWNER_WRITE);
				//perms.add(PosixFilePermission.OWNER_EXECUTE);
				//add group permissions
				perms.add(PosixFilePermission.GROUP_READ);
				perms.add(PosixFilePermission.GROUP_WRITE);
				//perms.add(PosixFilePermission.GROUP_EXECUTE);
				//add others permissions
				perms.add(PosixFilePermission.OTHERS_READ);
				perms.add(PosixFilePermission.OTHERS_WRITE);
				//perms.add(PosixFilePermission.OTHERS_EXECUTE);
	        
				Files.setPosixFilePermissions(Paths.get(result_name), perms);
			}
			// Скопировать /word/document.xml в буфер
		    try (FileSystem zipFileSys = createZipFileSystem(result_name, false)) {
		        Path path = zipFileSys.getPath("/word/document.xml");
		        body = Files.readAllLines(path);
		    }
		    // Корректировка body
		    List<String> res = Rep2.build(body, aParam);
		    if (res == null) {
				// Формирование отчета об ошибке
			    rest.setStatus("error");
			    rest.setCode(610);
			    rest.setMessage("The report \'Check list\' is failure (DB error)");
			    rest.setData("The report \'Check list\' is failure (DB error)");
		    } else if (res.get(0).equals("error")) {
				// Формирование отчета об ошибке
			    rest.setStatus("error");
			    rest.setCode(610);
			    rest.setMessage("The report \'Check list\' is failure (DB error)");
			    rest.setData(res.get(1));
		    } else {
		    	// Запись body в файл
		    	try (FileSystem zipFileSys = createZipFileSystem(result_name, false)) {
		    		Path path = zipFileSys.getPath("/word/document.xml");
		    		Files.write(path, body, Charset.forName("UTF-8"), StandardOpenOption.CREATE);
		    	}
		    	// Формирование успешного результата
		    	rest.setStatus("success");
		    	rest.setCode(200);
		    	rest.setMessage("OK");
		    	rest.setData(result_name);
		    }
		} catch(IOException  ex) {
			ex.printStackTrace();
			System.out.println("REP: " + ex.toString());
			// Формирование отчета об ошибке
		    rest.setStatus("error");
		    rest.setCode(602);
		    rest.setMessage("The report \'Check list\' is failure (IO-error)");
		    rest.setData(ex.toString());
		} catch(Exception  ex) {
			ex.printStackTrace();
			System.out.println("REPxx: " + ex.toString());
			// Формирование отчета об ошибке
		    rest.setStatus("error");
		    rest.setCode(610);
		    rest.setMessage("The report \'Check list\' is failure (DB error)");
		    rest.setData(ex.toString());
		}
		return rest;
	}
	
	// Отчет #3 (Резюме проекта)
	public static restAnswer doReport3(String aParam) {
		restAnswer rest = new restAnswer();	// Результат
		List<String> body = new ArrayList<String>();
		try {
			// Скопировать шаблон в результирующий файл
			String result_name = new String(System.getProperty("java.io.tmpdir") + "/" +
					Report.rep3_prefix + Report.getUUID() + Report.rep3_suffix);
			Files.copy(Paths.get(
					System.getProperty("catalina.base") + Report.templates_dir + rep3_template), 
					Paths.get(result_name), 
					StandardCopyOption.REPLACE_EXISTING);
			// Установка атрибутов результирующего файла (для UNIX)
			if (isPosix) {
				Set<PosixFilePermission> perms = new HashSet<PosixFilePermission>();
				//add owners permission
				perms.add(PosixFilePermission.OWNER_READ);
				perms.add(PosixFilePermission.OWNER_WRITE);
				//perms.add(PosixFilePermission.OWNER_EXECUTE);
				//add group permissions
				perms.add(PosixFilePermission.GROUP_READ);
				perms.add(PosixFilePermission.GROUP_WRITE);
				//perms.add(PosixFilePermission.GROUP_EXECUTE);
				//add others permissions
				perms.add(PosixFilePermission.OTHERS_READ);
				perms.add(PosixFilePermission.OTHERS_WRITE);
				//perms.add(PosixFilePermission.OTHERS_EXECUTE);
	        
				Files.setPosixFilePermissions(Paths.get(result_name), perms);
			}
			// Скопировать /word/document.xml в буфер
		    try (FileSystem zipFileSys = createZipFileSystem(result_name, false)) {
		        Path path = zipFileSys.getPath("/word/document.xml");
		        body = Files.readAllLines(path);
		    }
		    // Корректировка body
		    List<String> res = Rep3.build(body, aParam);
		    if (res == null) {
				// Формирование отчета об ошибке
			    rest.setStatus("error");
			    rest.setCode(610);
			    rest.setMessage("The report \'Check list\' is failure (DB error)");
			    rest.setData("The report \'Check list\' is failure (DB error)");
		    } else if (res.get(0).equals("error")) {
				// Формирование отчета об ошибке
			    rest.setStatus("error");
			    rest.setCode(610);
			    rest.setMessage("The report \'Check list\' is failure (DB error)");
			    rest.setData(res.get(1));
		    } else {
		    	// Запись body в файл
		    	try (FileSystem zipFileSys = createZipFileSystem(result_name, false)) {
		    		Path path = zipFileSys.getPath("/word/document.xml");
		    		Files.write(path, body, Charset.forName("UTF-8"), StandardOpenOption.CREATE);
		    	}
		    	// Формирование успешного результата
		    	rest.setStatus("success");
		    	rest.setCode(200);
		    	rest.setMessage("OK");
		    	rest.setData(result_name);
		    }
		} catch(IOException  ex) {
			ex.printStackTrace();
			System.out.println("REP: " + ex.toString());
			// Формирование отчета об ошибке
		    rest.setStatus("error");
		    rest.setCode(602);
		    rest.setMessage("The report \'Check list\' is failure (IO-error)");
		    rest.setData(ex.toString());
		} catch(Exception  ex) {
			ex.printStackTrace();
			System.out.println("REPxx: " + ex.toString());
			// Формирование отчета об ошибке
		    rest.setStatus("error");
		    rest.setCode(610);
		    rest.setMessage("The report \'Check list\' is failure (DB error)");
		    rest.setData(ex.toString());
		}
		return rest;
	}
	
	// Отчет #4 (Анкета проекта)
	public static restAnswer doReport4(String aParam) {
		restAnswer rest = new restAnswer();	// Результат
		List<String> body1 = new ArrayList<String>();
		List<String> body2 = new ArrayList<String>();
		try {
			// Скопировать шаблон в результирующий файл
			String result_name = new String(System.getProperty("java.io.tmpdir") + "/" +
					Report.rep4_prefix + Report.getUUID() + Report.rep4_suffix);
			Files.copy(Paths.get(
					System.getProperty("catalina.base") + Report.templates_dir + rep4_template), 
					Paths.get(result_name), 
					StandardCopyOption.REPLACE_EXISTING);
			// Установка атрибутов результирующего файла (для UNIX)
			if (isPosix) {
				Set<PosixFilePermission> perms = new HashSet<PosixFilePermission>();
				//add owners permission
				perms.add(PosixFilePermission.OWNER_READ);
				perms.add(PosixFilePermission.OWNER_WRITE);
				//perms.add(PosixFilePermission.OWNER_EXECUTE);
				//add group permissions
				perms.add(PosixFilePermission.GROUP_READ);
				perms.add(PosixFilePermission.GROUP_WRITE);
				//perms.add(PosixFilePermission.GROUP_EXECUTE);
				//add others permissions
				perms.add(PosixFilePermission.OTHERS_READ);
				perms.add(PosixFilePermission.OTHERS_WRITE);
				//perms.add(PosixFilePermission.OTHERS_EXECUTE);
	        
				Files.setPosixFilePermissions(Paths.get(result_name), perms);
			}
			// Скопировать /xl/sharedStrings.xml и /xl/worksheets/sheet1.xml в два буфера
		    try (FileSystem zipFileSys = createZipFileSystem(result_name, false)) {
		        Path path1 = zipFileSys.getPath("/xl/sharedStrings.xml");
		        body1 = Files.readAllLines(path1);
		        Path path2 = zipFileSys.getPath("/xl/worksheets/sheet1.xml");
		        body2 = Files.readAllLines(path2);
		    }
		    // Корректировка body1 и body2
		    String res = Rep4.build(body1, body2, aParam);
		    if (res.trim().length() > 0) {
				// Формирование отчета об ошибке
			    rest.setStatus("error");
			    rest.setCode(610);
			    rest.setMessage("The report \'Application\' is failure (DB error)");
			    rest.setData(res);
		    } else {
		    	// Запись body в файл
		    	try (FileSystem zipFileSys = createZipFileSystem(result_name, false)) {
		    		Path path1 = zipFileSys.getPath("/xl/sharedStrings.xml");
		    		Files.write(path1, body1, Charset.forName("UTF-8"), StandardOpenOption.CREATE);
		    		Path path2 = zipFileSys.getPath("/xl/worksheets/sheet1.xml");
		    		Files.write(path2, body2, Charset.forName("UTF-8"), StandardOpenOption.CREATE);
		    	}
		    	// Формирование успешного результата
		    	rest.setStatus("success");
		    	rest.setCode(200);
		    	rest.setMessage("OK");
		    	rest.setData(result_name);
		    }
		} catch(IOException  ex)
		{
			ex.printStackTrace();
			System.out.println("REP: " + ex.toString());
			// Формирование отчета об ошибке
		    rest.setStatus("error");
		    rest.setCode(602);
		    rest.setMessage("The report \'Resume\' is failure (IO-error)");
		    rest.setData(ex.toString());
		} catch(Exception  ex) {
			ex.printStackTrace();
			System.out.println("REPxx: " + ex.toString());
			// Формирование отчета об ошибке
		    rest.setStatus("error");
		    rest.setCode(610);
		    rest.setMessage("The report \'Resume\' is failure (DB error)");
		    rest.setData(ex.toString());
		}
		return rest;
	}

	// Отчет #5 (Отчет по всем заявкам)
	public static restAnswer doReport5(String aParam) {
		
		// Результат
		restAnswer rest = new restAnswer();
		
		// Разбор параметра на отдельные идентификаторы
		String [] IDs = aParam.split("\\s*,\\s*");
		
		List<String> body1 = new ArrayList<String>();
		List<String> body2 = new ArrayList<String>();
		try {
			// Скопировать шаблон в результирующий файл
			String result_name = new String(System.getProperty("java.io.tmpdir") + "/" +
					Report.rep5_prefix + Report.getUUID() + Report.rep5_suffix);
			Files.copy(Paths.get(
					System.getProperty("catalina.base") + Report.templates_dir + rep5_template), 
					Paths.get(result_name), 
					StandardCopyOption.REPLACE_EXISTING);
			// Установка атрибутов результирующего файла (для UNIX)
			if (isPosix) {
				Set<PosixFilePermission> perms = new HashSet<PosixFilePermission>();
				//add owners permission
				perms.add(PosixFilePermission.OWNER_READ);
				perms.add(PosixFilePermission.OWNER_WRITE);
				//perms.add(PosixFilePermission.OWNER_EXECUTE);
				//add group permissions
				perms.add(PosixFilePermission.GROUP_READ);
				perms.add(PosixFilePermission.GROUP_WRITE);
				//perms.add(PosixFilePermission.GROUP_EXECUTE);
				//add others permissions
				perms.add(PosixFilePermission.OTHERS_READ);
				perms.add(PosixFilePermission.OTHERS_WRITE);
				//perms.add(PosixFilePermission.OTHERS_EXECUTE);
	        
				Files.setPosixFilePermissions(Paths.get(result_name), perms);
			}
			// Скопировать /xl/sharedStrings.xml и /xl/worksheets/sheet1.xml в два буфера
		    try (FileSystem zipFileSys = createZipFileSystem(result_name, false)) {
		        Path path1 = zipFileSys.getPath("/xl/sharedStrings.xml");
		        body1 = Files.readAllLines(path1);
		        Path path2 = zipFileSys.getPath("/xl/worksheets/sheet1.xml");
		        body2 = Files.readAllLines(path2);
		    }
		    // Корректировка body1 и body2
		    String res = Rep5.build(body1, body2, IDs);
		    if (res.trim().length() > 0) {
				// Формирование отчета об ошибке
			    rest.setStatus("error");
			    rest.setCode(610);
			    rest.setMessage("The report \'Application\' is failure (DB error)");
			    rest.setData(res);
		    } else {
		    	// Запись body в файл
		    	try (FileSystem zipFileSys = createZipFileSystem(result_name, false)) {
		    		Path path1 = zipFileSys.getPath("/xl/sharedStrings.xml");
		    		Files.write(path1, body1, Charset.forName("UTF-8"), StandardOpenOption.CREATE);
		    		Path path2 = zipFileSys.getPath("/xl/worksheets/sheet1.xml");
		    		Files.write(path2, body2, Charset.forName("UTF-8"), StandardOpenOption.CREATE);
		    	}
		    	// Формирование успешного результата
		    	rest.setStatus("success");
		    	rest.setCode(200);
		    	rest.setMessage("OK");
		    	rest.setData(result_name);
		    }
		} catch(IOException  ex)
		{
			ex.printStackTrace();
			System.out.println("REP: " + ex.toString());
			// Формирование отчета об ошибке
		    rest.setStatus("error");
		    rest.setCode(602);
		    rest.setMessage("The report \'Resume\' is failure (IO-error)");
		    rest.setData(ex.toString());
		} catch(Exception  ex) {
			ex.printStackTrace();
			System.out.println("REPxx: " + ex.toString());
			// Формирование отчета об ошибке
		    rest.setStatus("error");
		    rest.setCode(610);
		    rest.setMessage("The report \'Resume\' is failure (DB error)");
		    rest.setData(ex.toString());
		}
		return rest;
	}
	
	// Работа с ZIP-файлом как с файловой системой
	private static FileSystem createZipFileSystem(String zipFilename,
            boolean create)
            throws IOException {
			// convert the filename to a URI
			final Path path = Paths.get(zipFilename);
			final URI uri = URI.create("jar:file:" + path.toUri().getPath());

			final Map<String, String> env = new HashMap<>();
			if (create) {
				env.put("create", "true");
			}
			return FileSystems.newFileSystem(uri, env);
	}
	
	// Заменить в строковой коллекции arr все строки someStr на otherStr
	public static void zReplaceAll(List<String> arr, String someStr, String otherStr) {
		String newStr;
		for (int i = 0; i < arr.size(); i++) {
			if (arr.get(i).contains(someStr)) {
				newStr = arr.get(i).replaceAll(someStr, otherStr);
				arr.set(i, newStr);
			}
		}
	}
	
	// Извлечь поле из элемента коллекции
	public static Object getObject(DBObject o, String fieldName) {

	    //final String[] fieldParts = StringUtils.split(fieldName, '.');
	    final String[] fieldParts = fieldName.split("\\.");

	    int i = 1;
	    Object val = o.get(fieldParts[0]);

	    while(i < fieldParts.length && val instanceof DBObject) {
	        val = ((DBObject)val).get(fieldParts[i]);
	        i++;
	    }

	    return val;
	}
	
	// Неопределеное поле заменяется на пустую строку
	public static String legal(DBObject o, String fieldName) {
		if (o == null) return "";
		Object val = getObject(o, fieldName);
		return val == null? "": val.toString();
	}

	// Неопределеное поле заменяется на знаки вопросов
	public static String exam(DBObject o, String fieldName) {
		if (o == null) return "-";
		Object val = getObject(o, fieldName);
		return val == null? "-": val.toString();
	}
	
	// Неопределеное поле заменяется на знаки вопросов | ДА/НЕТ
	public static String exam3(DBObject o, String fieldName) {
		if (o == null) return "-";
		Object val = getObject(o, fieldName);
		return val == null? "-": (boolean)val? "ДА": "НЕТ";
	}

	// Неопределеное поле заменяется на пустую строку
	public static String legal(BasicDBList list, int index, String fieldName) {
		if (list == null) return "";
		DBObject o = (DBObject)list.get(index);
		if (o == null) return "";
		Object val = getObject(o, fieldName);
		return val == null? "": val.toString();
	}
	
	// Неопределеное поле заменяется на знаки вопросов
	public static String exam(BasicDBList list, int index, String fieldName) {
		if (list == null) return "-";
		DBObject o = (DBObject)list.get(index);
		if (o == null) return "-";
		Object val = getObject(o, fieldName);
		return val == null? "-": val.toString();
	}
	
	// Преобразование строки в целое
	public static int zint(String val) {
		int res;
		try {
			res = val.trim().length() == 0? 0: Integer.parseInt(val);
		} catch (Exception  ex) {
			res = 0;
		}
		return res;
	}

	// Преобразование строки в целое
	public static float zfloat(String val) {
		float res;
		try {
			res = val.trim().length() == 0? 0: Float.parseFloat(val);
		} catch (Exception  ex) {
			res = 0;
		}
		return res;
	}
	
	// Получение UUID для имени файла
	private static String getUUID() {
		UUID id = UUID.randomUUID();
		return id.toString().replaceAll("-","");
	}

}
