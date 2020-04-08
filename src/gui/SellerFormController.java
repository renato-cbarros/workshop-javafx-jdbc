package gui;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import model.entities.Department;
import model.entities.Seller;
import model.exceptions.ValidationExceptions;
import model.service.DepartmentService;
import model.service.SellerService;

public class SellerFormController implements Initializable {

	private Seller entity;

	private SellerService sellerService;

	private DepartmentService departmentService;

	private List<DataChangeListener> dataChangeListener = new ArrayList<>();

	private ObservableList<Department> obsListDepartment;

	@FXML
	private TextField txtId;

	@FXML
	private TextField txtName;

	@FXML
	private TextField txtEmail;

	@FXML
	private DatePicker dpBirthDate;

	@FXML
	private TextField txtBaseSalary;

	@FXML
	private Label lblErrorName;

	@FXML
	private Label lblErrorEmail;

	@FXML
	private Label lblErrorBirthDate;

	@FXML
	private Label lblErrorBaseSalary;

	@FXML
	private ComboBox<Department> cBDeparment;

	@FXML
	private Button btSave;

	@FXML
	private Button btCancel;

	@FXML
	public void OnBtSaveAction(ActionEvent event) {

		if (entity == null) {
			throw new IllegalStateException("Seller was null");
		}
		if (sellerService == null) {
			throw new IllegalStateException("Service was null");
		}

		try {
			entity = getFormData();
			sellerService.saveOrUpdate(entity);
			notifyDataChangerListener();
			Utils.currentStage(event).close();
		} catch (ValidationExceptions e) {
			setErrorMessages(e.getErrors());
		} catch (DbException e) {
			Alerts.showAlert("Error saving object", null, e.getMessage(), AlertType.ERROR);
		}

	}

	@FXML
	private void OnBtCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}

	@Override
	public void initialize(URL url, ResourceBundle rB) {
		initializeNodes();
	}

	public void subrscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListener.add(listener);
	}

	public void loadAssociatedObjects() {
		if (departmentService == null) {
			throw new IllegalStateException("Department Service was null");
		}
		List<Department> listDepartment = departmentService.findAll();
		obsListDepartment = FXCollections.observableArrayList(listDepartment);
		cBDeparment.setItems(obsListDepartment);
	}

	private void initializeComboBoxDepartment() {
		Callback<ListView<Department>, ListCell<Department>> factory = lv -> new ListCell<Department>() {
			@Override
			protected void updateItem(Department item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getName());
			}
		};
		cBDeparment.setCellFactory(factory);
		cBDeparment.setButtonCell(factory.call(null));
	}

	private void notifyDataChangerListener() {
		for (DataChangeListener listener : dataChangeListener) {
			listener.onDataChanged();
		}

	}

	public void setSeller(Seller entity) {
		this.entity = entity;
	}

	public void setServices(SellerService sellerService, DepartmentService departmentService) {
		this.sellerService = sellerService;
		this.departmentService = departmentService;
	}

	private Seller getFormData() {
		Seller obj = new Seller();

		ValidationExceptions exception = new ValidationExceptions("Validation error");

		obj.setId(Utils.tryParseToInt(txtId.getText()));

		if (txtName.getText() == null || txtName.getText().equals("")) {
			exception.addErrors("name", "Field can't be empty");
		}
		obj.setName(txtName.getText());

		if (exception.getErrors().size() > 0) {
			throw exception;
		}

		return obj;
	}

	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtName, 100);
		Constraints.setTextFieldMaxLength(txtEmail, 70);
		Constraints.setTextFieldDouble(txtBaseSalary);
		Utils.formatDatePicker(dpBirthDate, "dd/MM/yyyy");
		
		initializeComboBoxDepartment();
	}

	public void updateFormData() {

		if (entity == null) {
			throw new IllegalStateException("Entity department was null");
		}

		txtId.setText(String.valueOf(entity.getId()));
		txtName.setText(entity.getName());
		txtEmail.setText(entity.getEmail());
		Locale.setDefault(Locale.US);
		txtBaseSalary.setText(String.format("%.2f", entity.getBaseSalary()));
		if (entity.getBaseSalary() != null) {
			dpBirthDate.setValue(LocalDate.ofInstant(entity.getBirthDate().toInstant(), ZoneId.systemDefault()));
		}
		if (entity.getDepartment() != null) {
			cBDeparment.setValue(entity.getDepartment());
		}else {
			cBDeparment.getSelectionModel().selectFirst();
		}

	}

	public void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();

		if (fields.contains("name")) {
			lblErrorName.setText(errors.get("name"));
		}
	}

}
