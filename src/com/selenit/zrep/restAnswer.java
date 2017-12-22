// Valerii Zinovev, Perm, 04 aug 2017
// ������������ ������ ��� XML/JSON-�������
package com.selenit.zrep;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "restAnswer")
public class restAnswer {
	// ������ ������ "success" ��� "error"
	private String status;
	// ��� ������ 200 - ��
	private int code;
	// ��������� - ��� ��� ������, ������, ��������� �� ������
	private String message;
	// ������ - ������, ��������, ������ ��� �����-������
	private String data;
	
	// �����������
	public restAnswer() {
		status = "error";
		code = 418;
		message = "I am a teapot";
		data = "";
	}
	
	// status
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	// code
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	
	// message
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	// data
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}

}
