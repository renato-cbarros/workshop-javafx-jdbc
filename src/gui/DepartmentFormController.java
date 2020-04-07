package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;
import model.service.DepartmentService;

public class DepartmentFormController implements Initializable {

	private Department entity;
	
	private DepartmentService service;
	
	private List<DataChangeListener> dataChangeListener=  new ArrayList<>();
	
	@FXML
	private TextField txtId;
	
	@FXML
	private TextField txtName;
	
	@FXML
	private Label lblErrorName;
	
	@FXML
	private Button btSave;
	
	@FXML
	private Button btCancel;
	
	public void setDepartment(Department entity) {
		this.entity = entity;
	}
	
	public void setDepartmentService(DepartmentService service) {
		this.service = service;
	}
	
	@FXML
	public void OnBtSaveAction(ActionEvent event) {
		
		if (entity == null) {
			throw new IllegalStateException("Department was null");
		}
		if (service == null) {
			throw new IllegalStateException("Service was null");
		}
		
		try {
			entity = getFormData();
			service.saveOrUpdate(entity);
			notifyDataChangerListener();
			Utils.currentStage(event).close();
		} catch (DbException e) {
			Alerts.showAlert("DB Error", null, e.getMessage(), AlertType.ERROR);
		}

	}
	
	private void notifyDataChangerListener() {
		for (DataChangeListener listener :  dataChangeListener) {
			listener.onDataChanged();
		}
		
	}

	private Department getFormData() {
		return new Department(gui.util.Utils.tryParseToInt(txtId.getText()), 
				txtName.getText());
	}

	@FXML
	private void OnBtCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}
	
	public void subrscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListener.add(listener);
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rB) {
		initializeNodes();
		
	}
	
	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtName, 30);
	}

	public void updateFormData () {
		
		if (entity == null) {
			throw new IllegalStateException("Entity department was null");
		}
		
		txtId.setText(String.valueOf(entity.getId()));
		txtName.setText(entity.getName());
	}
	
}
