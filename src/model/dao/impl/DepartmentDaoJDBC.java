package model.dao.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.mysql.jdbc.PreparedStatement;

import db.DB;
import db.DbException;
import model.dao.DepartmentDao;
import model.entities.Department;

public class DepartmentDaoJDBC implements DepartmentDao{
	
	protected Connection conn;
	
	public DepartmentDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(Department obj) {
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st = (PreparedStatement) conn.prepareStatement(
					"INSERT INTO department " + 
					"(Name) " + 
					"VALUES " + 
					"(?)", Statement.RETURN_GENERATED_KEYS);
			
			st.setString(1, obj.getName());

			int rowsAffected = st.executeUpdate();
			
			if (rowsAffected > 0) {
				rs = st.getGeneratedKeys();
				if (rs.next()) {
					obj.setId(rs.getInt(1));
				}
			} else {
				throw new DbException("unexpected error! ");
			}
			
		} catch (Exception e) {
			new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
		
	}

	@Override
	public void update(Department obj) {
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st = (PreparedStatement) conn.prepareStatement(
					"UPDATE department " + 
					"SET Name = ? " + 
					"WHERE Id = ?");
			
			st.setString(1, obj.getName());
			st.setInt(2, obj.getId());

			int rowsAffected = st.executeUpdate();
			
			if (rowsAffected == 0) {
				throw new DbException("0 Rows affected !");
			}
			
		} catch (Exception e) {
			new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
		
	}

	@Override
	public void delete(Department obj) {
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st = (PreparedStatement) conn.prepareStatement("DELETE FROM department WHERE Id = ?");
			
			st.setInt(1, obj.getId());

			int rows = st.executeUpdate();			
			
			if (rows == 0) {
				throw new DbException("0 Rows affected !");
			}
			
		} catch (Exception e) {
			new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

	@Override
	public Department findById(int id) {
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st = (PreparedStatement) conn.prepareStatement( 
					"SELECT * " +
					"FROM department " + 
					"WHERE Id = ?" );
			
			st.setInt(1, id);
			rs = st.executeQuery();
			
			if (rs.next()) {
				Department dep = imprementDepartament(rs);
				return dep;
			}
			return null;
			
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		}finally {
			DB.closeStatement(st);;
			DB.closeResultSet(rs);
		}
	}

	@Override
	public List<Department> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st = (PreparedStatement) conn.prepareStatement( 
					"SELECT * " + 
					"FROM department " + 
					"ORDER BY Id" );
			
			rs = st.executeQuery();
			
			List<Department> departmentList = new ArrayList<>();

			while (rs.next()) {
				
				Department dep = imprementDepartament(rs);

				departmentList.add(dep);

			}
			return departmentList;
			
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		}finally {
			DB.closeStatement(st);;
			DB.closeResultSet(rs);
		}
	}

	protected Department imprementDepartament(ResultSet rs) throws SQLException {
		Department departament = new Department();
		departament.setId(rs.getInt("Id"));
		departament.setName(rs.getString("Name"));	
		return departament;
	}

	
}
