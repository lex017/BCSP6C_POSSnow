package applicationForm;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import connect_database.MysqlConnect;
import includeClass.ImageRender;
import includeClass.PasswordHashing;
import includeClass.Validation;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.*;
import java.text.SimpleDateFormat;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class Panelemployee extends javax.swing.JPanel {

    Connection conn = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    
    private String ImgPath = null;
    private String oldpassword;
    private String oldUser;

    public Panelemployee() {
        initComponents();
        txtSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "ຄົ້ນຫາ");
        txtSearch.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, new FlatSVGIcon("image/search1.svg"));
        txtTelephone.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "021 123xxxxx");
        
        //ສະແດງລະຫັດຜ່ານ ແລະ ສະແດງສັນຍາລັກເມືອປຸ່ມຢູ່ໃນສະຖານະ capslock
        txtPassword.putClientProperty(FlatClientProperties.STYLE, "showRevealButton:true;" + "showCapsLock:true");

        //ປ່ຽນສີພື້ນຫົວຕາຕະລາງ
        JTableHeader header = jTable1.getTableHeader();
        header.setFont(new Font("Lao_SomVang", Font.PLAIN, 14));
        header.setOpaque(false);
        header.setBackground(new Color(108, 117, 125));
        header.setForeground(new Color(243, 243, 243));

        //ສະແດງຜົນຢູ່ກາງຖັນ
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        jTable1.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        jTable1.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        jTable1.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        jTable1.getColumnModel().getColumn(9).setCellRenderer(centerRenderer);
        
        //ສະແດງຜົນຢູ່ດ້ານຂວາຂອງຖັນ
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        jTable1.getColumnModel().getColumn(8).setCellRenderer(rightRenderer);
        jTable1.getColumnModel().getColumn(12).setCellRenderer(rightRenderer);
        
        jTable1.getColumnModel().getColumn(13).setCellRenderer(new ImageRender());
        
        //visible
        jTable1.removeColumn(jTable1.getColumnModel().getColumn(11));

        //ເສັ້ນຕາຕະລາງ
        jTable1.setShowGrid(false);
        jTable1.setShowHorizontalLines(true);
        jTable1.setShowVerticalLines(true);
        jTable1.setGridColor(new Color(139, 138, 137));

        //ເຊື່ອມຕໍ່ ຖານຂໍ້ມູນ
        conn = MysqlConnect.connectDB();
        tableUpdate();
        listprovince();
        autoID();

        //ໃຫ້ປຸ່ມແກ້ໄຂ ແລະ ລືບກົດບໍ່ໄດ້
        btnEdit.setEnabled(false);
        btnDelete.setEnabled(false);
        
        txtMale.setActionCommand("ຊາຍ");
        txtFemale.setActionCommand("ຍິງ");
        txtanother.setActionCommand("ບໍ່ລະບຸ");

    }
      
    //
    private void listprovince(){
        try {
            String sql = "select * from province";
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            txtProvince.removeAllItems();
            while(rs.next()){
                txtProvince.addItem(rs.getString("province_name"));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
        }
    }
    
    //ປັບຂະໜາດຮູບພາບ
    private ImageIcon resizeImage(String imagePath, byte[] pic) {
        ImageIcon myImage;
        if (imagePath != null) {
            myImage = new ImageIcon(imagePath);
        } else {
            myImage = new ImageIcon(pic);
        }
        Image img = myImage.getImage();
        Image img2 = img.getScaledInstance(picture.getWidth(), picture.getHeight(), Image.SCALE_SMOOTH);
        ImageIcon image = new ImageIcon(img2);
        return image;
    }
    
    
    
    //ສ້າງເມັດເຈັນເຍີເລດ ລະະຫັດ
    private void autoID() {
        try {
            String sql = "SELECT max(emp_id) FROM employee";
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            if (rs.next()) {
                int id = Integer.parseInt(rs.getString(1).substring(4, 6));
                id++;
                txtid.setText("EMP" + String.format("%03d", id));
            } else {
                txtid.setText("EMP001");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    //ຂຽນເມັດທອດສະແດງຄ່າໃນຕາຕະລາງ
    private void tableUpdate() {
        try {
            String sql = "SELECT *FROM employee ORDER BY emp_id DESC";

            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();

            DefaultTableModel d = (DefaultTableModel) jTable1.getModel();
            jTable1.setRowHeight(100);
            //ລືບຂໍ້ມູນໃນຕາຕະລາງ
            d.setRowCount(0);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            while (rs.next()) {
                d.addRow(new Object[]{
                    rs.getString("emp_id"),
                    rs.getString("emp_name"),
                    rs.getString("emp_lname"),
                    rs.getString("gender"),
                    dateFormat.format(rs.getDate("date_of_b")),
                    rs.getString("village"),
                    rs.getString("city"),
                    rs.getString("province"),
                    rs.getString("tel"),
                    dateFormat.format(rs.getDate("start_date")),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("status"),
                    rs.getBytes("picture")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
        }
    }

    //ລືບຂໍ້ມູນໃນຟອມ
    private void clearForm() {
        autoID();
        txtFirstname.setText("");
        txtLastname.setText("");
        gender.clearSelection();
        txtDateofBirth.setDate(null);
        txtStartwork.setDate(null);
        txtProvince.setSelectedIndex(0);
        txtVillage.setText("");
        txtTelephone.setText("");
        txtUser.setText("");
        txtPassword.setText("");
        txtStatus.setSelectedIndex(0);
        picture.setIcon(null);
        btnAdd.setEnabled(true);
        btnEdit.setEnabled(false);
        btnDelete.setEnabled(false);
        
        jTable1.clearSelection();
    }
    
    //ເມັດທອດກວດສອບຟອມ
    private boolean checkInput(){
        return txtFirstname.getText().isEmpty()
        || txtLastname.getText().isEmpty()
        || gender.getSelection() == null
        || txtDateofBirth.getDate() == null
        || txtStartwork.getDate() == null
        || txtVillage.getText().isEmpty()
        || txtTelephone.getText().isEmpty()
        || txtUser.getText().isEmpty()
        || txtPassword.getText().isEmpty()
        || picture.getIcon() == null;
                
    }
    
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        gender = new javax.swing.ButtonGroup();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        txtSearch = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtFirstname = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtid = new javax.swing.JTextField();
        btnCancel = new javax.swing.JButton();
        btnAdd = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        txtLastname = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtVillage = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtTelephone = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        picture = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txtUser = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        btnBrownImage = new javax.swing.JButton();
        txtanother = new javax.swing.JRadioButton();
        txtMale = new javax.swing.JRadioButton();
        txtFemale = new javax.swing.JRadioButton();
        txtStartwork = new com.toedter.calendar.JDateChooser();
        txtDateofBirth = new com.toedter.calendar.JDateChooser();
        txtStatus = new javax.swing.JComboBox<>();
        txtProvince = new javax.swing.JComboBox<>();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        txtCity = new javax.swing.JComboBox<>();
        txtPassword = new javax.swing.JPasswordField();

        jTable1.setFont(new java.awt.Font("Lao_SomVang", 0, 14)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "ລະຫັດ", "ຊື່", "ນາມສະກຸນ", "ເພດ", "ວັນເດືອນປິເກຶດ", "ບ້ານ", "ເມືອງ", "ແຂວງ", "ເບີໂທລະສັບ", "ມື້ເຂັ້າການ", "ບັນຊີເຂັ້າໃຊ້", "ລະຫັດຜ່ານ", "ສະຖານະ", "ຮູບພາບ"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        txtSearch.setFont(new java.awt.Font("Lao_SomVang", 0, 14)); // NOI18N

        jPanel2.setForeground(new java.awt.Color(204, 204, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "ຈັດການຂໍ້ມູນພະນັກງານ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lao_SomVang", 0, 16))); // NOI18N
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setFont(new java.awt.Font("Lao_SomVang", 0, 14)); // NOI18N
        jLabel2.setText("ຊື່ພະນັກງານ");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 90, -1, -1));

        txtFirstname.setFont(new java.awt.Font("Lao_SomVang", 0, 14)); // NOI18N
        jPanel1.add(txtFirstname, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 90, 240, -1));

        jLabel3.setFont(new java.awt.Font("Lao_SomVang", 0, 14)); // NOI18N
        jLabel3.setText("ລະຫັດພະນັກງານ");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 40, 80, -1));

        txtid.setEditable(false);
        txtid.setFont(new java.awt.Font("Lao_SomVang", 0, 14)); // NOI18N
        jPanel1.add(txtid, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 40, 240, -1));

        btnCancel.setBackground(new java.awt.Color(21, 115, 71));
        btnCancel.setFont(new java.awt.Font("Lao_SomVang", 0, 14)); // NOI18N
        btnCancel.setForeground(new java.awt.Color(253, 254, 254));
        btnCancel.setText("ຍົກເລີກ");
        btnCancel.setMargin(new java.awt.Insets(2, 3, 3, 3));
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });
        jPanel1.add(btnCancel, new org.netbeans.lib.awtextra.AbsoluteConstraints(950, 260, 70, -1));

        btnAdd.setBackground(new java.awt.Color(11, 94, 215));
        btnAdd.setFont(new java.awt.Font("Lao_SomVang", 0, 14)); // NOI18N
        btnAdd.setForeground(new java.awt.Color(251, 238, 230));
        btnAdd.setText("ເພີ່ມ");
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });
        jPanel1.add(btnAdd, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 260, 70, -1));

        btnEdit.setBackground(new java.awt.Color(255, 201, 12));
        btnEdit.setFont(new java.awt.Font("Lao_SomVang", 0, 14)); // NOI18N
        btnEdit.setForeground(new java.awt.Color(28, 31, 35));
        btnEdit.setText("ແກ້ໄຂ");
        btnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditActionPerformed(evt);
            }
        });
        jPanel1.add(btnEdit, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 260, 70, -1));

        btnDelete.setBackground(new java.awt.Color(187, 45, 59));
        btnDelete.setFont(new java.awt.Font("Lao_SomVang", 0, 14)); // NOI18N
        btnDelete.setForeground(new java.awt.Color(253, 254, 254));
        btnDelete.setText("ລືບ");
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });
        jPanel1.add(btnDelete, new org.netbeans.lib.awtextra.AbsoluteConstraints(870, 260, 70, -1));

        jLabel4.setFont(new java.awt.Font("Lao_SomVang", 0, 14)); // NOI18N
        jLabel4.setText("ນາມສະກຸນ");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 140, -1, -1));

        txtLastname.setFont(new java.awt.Font("Lao_SomVang", 0, 14)); // NOI18N
        jPanel1.add(txtLastname, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 140, 240, -1));

        jLabel5.setFont(new java.awt.Font("Lao_SomVang", 0, 14)); // NOI18N
        jLabel5.setText("ວັນເດືອນປີເກິດ");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 260, -1, -1));

        txtVillage.setFont(new java.awt.Font("Lao_SomVang", 0, 14)); // NOI18N
        jPanel1.add(txtVillage, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 200, 210, -1));

        jLabel6.setFont(new java.awt.Font("Lao_SomVang", 0, 14)); // NOI18N
        jLabel6.setText("ບ້ານ");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 200, -1, -1));

        txtTelephone.setFont(new java.awt.Font("Lao_SomVang", 0, 14)); // NOI18N
        jPanel1.add(txtTelephone, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 260, 210, -1));

        jLabel7.setFont(new java.awt.Font("Lao_SomVang", 0, 14)); // NOI18N
        jLabel7.setText("ບັນຊີເຂົ້າໃສ່");
        jPanel1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 150, -1, -1));

        picture.setBackground(new java.awt.Color(102, 102, 255));
        picture.setOpaque(true);
        jPanel1.add(picture, new org.netbeans.lib.awtextra.AbsoluteConstraints(1030, 40, 140, 170));

        jLabel8.setFont(new java.awt.Font("Lao_SomVang", 0, 14)); // NOI18N
        jLabel8.setText("ເບີໂທລະສັບ");
        jPanel1.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 260, -1, -1));

        txtUser.setFont(new java.awt.Font("Lao_SomVang", 0, 14)); // NOI18N
        jPanel1.add(txtUser, new org.netbeans.lib.awtextra.AbsoluteConstraints(780, 40, 220, -1));

        jLabel9.setFont(new java.awt.Font("Lao_SomVang", 0, 14)); // NOI18N
        jLabel9.setText("ເພດ");
        jPanel1.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 200, -1, -1));

        jLabel10.setFont(new java.awt.Font("Lao_SomVang", 0, 14)); // NOI18N
        jLabel10.setText("ແຂວງ");
        jPanel1.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 90, -1, -1));

        jLabel11.setFont(new java.awt.Font("Lao_SomVang", 0, 14)); // NOI18N
        jLabel11.setText("ມື້ເຂົ້າງານ");
        jPanel1.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 40, -1, -1));

        jLabel12.setFont(new java.awt.Font("Lao_SomVang", 0, 14)); // NOI18N
        jLabel12.setText("ເມືອງ");
        jPanel1.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 140, -1, -1));

        btnBrownImage.setBackground(new java.awt.Color(51, 51, 255));
        btnBrownImage.setFont(new java.awt.Font("Lao_SomVang", 0, 12)); // NOI18N
        btnBrownImage.setForeground(new java.awt.Color(253, 254, 254));
        btnBrownImage.setText("ເລືອກຮູບ");
        btnBrownImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBrownImageActionPerformed(evt);
            }
        });
        jPanel1.add(btnBrownImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(1030, 220, 140, -1));

        gender.add(txtanother);
        txtanother.setFont(new java.awt.Font("Lao_SomVang", 0, 12)); // NOI18N
        txtanother.setText("ບໍ່ລະບຸເພດ");
        jPanel1.add(txtanother, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 200, -1, -1));

        gender.add(txtMale);
        txtMale.setFont(new java.awt.Font("Lao_SomVang", 0, 12)); // NOI18N
        txtMale.setText("ຊາຍ");
        jPanel1.add(txtMale, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 200, -1, -1));

        gender.add(txtFemale);
        txtFemale.setFont(new java.awt.Font("Lao_SomVang", 0, 12)); // NOI18N
        txtFemale.setText("ຍຶງ");
        txtFemale.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFemaleActionPerformed(evt);
            }
        });
        jPanel1.add(txtFemale, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 200, -1, -1));

        txtStartwork.setDateFormatString("dd/MM/Y");
        txtStartwork.setFont(new java.awt.Font("Lao_SomVang", 0, 12)); // NOI18N
        jPanel1.add(txtStartwork, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 40, 210, 30));

        txtDateofBirth.setDateFormatString("dd/MM/Y");
        txtDateofBirth.setFont(new java.awt.Font("Lao_SomVang", 0, 12)); // NOI18N
        jPanel1.add(txtDateofBirth, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 260, 210, 30));

        txtStatus.setFont(new java.awt.Font("Lao_SomVang", 0, 12)); // NOI18N
        txtStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Admin", "Employee" }));
        jPanel1.add(txtStatus, new org.netbeans.lib.awtextra.AbsoluteConstraints(780, 140, 220, 30));

        txtProvince.setFont(new java.awt.Font("Lao_SomVang", 0, 12)); // NOI18N
        txtProvince.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtProvinceActionPerformed(evt);
            }
        });
        jPanel1.add(txtProvince, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 90, 210, 30));

        jLabel13.setFont(new java.awt.Font("Lao_SomVang", 0, 14)); // NOI18N
        jLabel13.setText("ບັນຊີເຂົ້າໃສ່");
        jPanel1.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 40, -1, -1));

        jLabel14.setFont(new java.awt.Font("Lao_SomVang", 0, 14)); // NOI18N
        jLabel14.setText("ລະຫັດຜ່ານ");
        jPanel1.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 100, -1, -1));

        txtCity.setFont(new java.awt.Font("Lao_SomVang", 0, 12)); // NOI18N
        txtCity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCityActionPerformed(evt);
            }
        });
        jPanel1.add(txtCity, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 140, 210, 30));

        txtPassword.setFont(new java.awt.Font("Lao_SomVang", 0, 12)); // NOI18N
        jPanel1.add(txtPassword, new org.netbeans.lib.awtextra.AbsoluteConstraints(780, 90, 220, 30));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(32, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 1200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(32, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 355, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1))
                .addGap(33, 33, 33))
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 229, Short.MAX_VALUE)
                .addGap(21, 21, 21))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        clearForm();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        //ກວດສອບວ່າປ້ອນຂໍ້ມູນຄົບ ຫຼື ບໍ່
        if(checkInput()){
            JOptionPane.showMessageDialog(this,"ກະລຸນາປ້ອນຂໍ້ມູນໃຫ້ຄົບຖ້ວຍດ້ວຍ","หว่างຫວ່າງເປົ່າ", JOptionPane.WARNING_MESSAGE);
            return;
        }
        //ກວດສອບເບີໂທລະສັบບຖືກຕາມຮູບແບບ ຫຼື ບໍ່
        if(Validation.telephoneValidation(txtTelephone.getText())){
            JOptionPane.showMessageDialog(this,"ກະລຸນາປ້ອນຂໍ້ມູນເບີໂທລະສັບໃຫ້ຖືກຕ້ອງດ້ວຍ","หว่างຫວ່າງເປົ່າ", JOptionPane.ERROR_MESSAGE);
            return;
        }
        //ກວດສອບບັນຊີເຂົ້າໃຊ້ວ່າຊໍ້າກັນຫຼືບໍ່
        try {
            String sql = "SELECT *FROM employee WHERE username=?";
            pst = conn.prepareStatement(sql);
            pst.setString(1, txtUser.getText());
            rs = pst.executeQuery();
            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "ບັນຊີເຂົ້າໃຊ້ນີ້ຖືກນໍາໃຊ້ແລ້ວ", "ຜິດພາດ", JOptionPane.ERROR_MESSAGE);
                txtUser.requestFocus();
                return;
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            return;
        }
        
        //ສ້າງ ອ໊ອບເຈັດ dateFormate ເພື່ອຈັດຮູບແບບ ວັນເດືອນປີເກີດ ແລະ ວັນເລີ່ມຕົ້ນເຮັດວຽກ
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        //ຮັບຮູບພາບ
        InputStream addPicture = null;
        try {
            addPicture = new FileInputStream(new File(ImgPath));
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, e);
        }

        //ເພີ່ມຂໍ້ມູນລົງຖານຂໍ້ມູນ
        try {
            String sql = "INSERT INTO employee VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

            pst = conn.prepareStatement(sql);
            pst.setString(1, txtid.getText());
            pst.setString(2, txtFirstname.getText());
            pst.setString(3, txtLastname.getText());
            pst.setString(4, gender.getSelection().getActionCommand());
            pst.setString(5, dateFormat.format(txtDateofBirth.getDate()));
            pst.setString(6, txtVillage.getText());
            pst.setString(7, txtCity.getSelectedItem().toString());
            pst.setString(8, txtProvince.getSelectedItem().toString());
            pst.setString(9, txtTelephone.getText());
            pst.setString(10, dateFormat.format(txtStartwork.getDate()));
            pst.setBlob(11, addPicture);
            pst.setString(12, txtUser.getText());
            pst.setString(13, PasswordHashing.doHashing(txtPassword.getText()));
            pst.setString(14, txtStatus.getSelectedItem().toString());

            if (pst.executeUpdate() > 0) {
                JOptionPane.showMessageDialog(this, "ຂໍ້ມູນຖືກບັນທຶກລົງໃນຖານຂໍ້ມູນສໍາເລັດ", "ສໍາເລັດ", JOptionPane.WIDTH, new FlatSVGIcon("image_svg/check.svg"));
                clearForm(); //ລືບຂໍ້ມູນໃນຟອມ
                tableUpdate(); //ສະແດງຂໍ້ມູນໃນຕາຕະລາງ
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
        }
    }//GEN-LAST:event_btnAddActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        DefaultTableModel d = (DefaultTableModel) jTable1.getModel();
        int selectIndex = jTable1.getSelectedRow();
        //ຖ້າກົດເມົ້າໃສ່ບໍ່ຖືກແຖວ
        if (selectIndex == -1) {
            return;
        }

        txtid.setText(d.getValueAt(selectIndex, 0).toString());
        txtFirstname.setText(d.getValueAt(selectIndex, 1).toString());
        txtLastname.setText(d.getValueAt(selectIndex, 2).toString());
        //ເພດ
        String gender;
        gender = d.getValueAt(selectIndex, 3).toString();
        if (gender.equals("ຊາຍ")) {
            txtMale.setSelected(true);
        }else if (gender.equals("ຍິງ")){
            txtFemale.setSelected(true);
        } else {
            txtanother.setSelected(true);
        }

        //ວັນເດືອນປີເກີດ ແລະ ວັນເດືອນປີເຂົ້າເຮັດວຽກ
        try {
            txtDateofBirth.setDate(new SimpleDateFormat("dd/MM/yyyy").parse(d.getValueAt(selectIndex, 4).toString()));
            txtStartwork.setDate(new SimpleDateFormat("dd/MM/yyyy").parse(d.getValueAt(selectIndex, 9).toString()));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
        }
        txtVillage.setText(d.getValueAt(selectIndex, 5).toString());
        txtProvince.setSelectedItem(d.getValueAt(selectIndex, 7).toString());
        txtCity.setSelectedItem(d.getValueAt(selectIndex, 6).toString());

        txtTelephone.setText(d.getValueAt(selectIndex, 8).toString());
        txtUser.setText(d.getValueAt(selectIndex, 10).toString());
        oldUser =d.getValueAt(selectIndex, 10).toString();
        oldpassword = (d.getValueAt(selectIndex, 11).toString());
        txtStatus.setSelectedItem(d.getValueAt(selectIndex, 12).toString());

        //ສະແດງຮູບ
        try {
            String sql = "SELECT picture FROM employee WHERE emp_id=?";
            pst = conn.prepareStatement(sql);
            pst.setString(1, d.getValueAt(selectIndex, 0).toString());
            rs = pst.executeQuery();
            if (rs.next()) {
                picture.setIcon(resizeImage(null, rs.getBytes("picture")));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
        }

        //ໃຫ້ປຸ່ມເພີ່ມໃຊ້ງານບໍ່ໄດ້
        btnAdd.setEnabled(false);
        btnEdit.setEnabled(true);
        btnDelete.setEnabled(true);
    }//GEN-LAST:event_jTable1MouseClicked

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
       String addPassword;
        InputStream addPicture = null;

        //ກວດສອບຄ່າຫວ່າງເປົ່າໃນຟອມ
        if (txtFirstname.getText().isEmpty()
                || txtLastname.getText().isEmpty()
                || txtVillage.getText().isEmpty()
                || txtTelephone.getText().isEmpty()
                || txtUser.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "ກະລຸນາປ້ອນຂໍ້ມູນໃຫ້ຄົບຖ້ວນດ້ວຍ", "ຄ່າຫວ່າງເປົ່າ", JOptionPane.WARNING_MESSAGE);
            return;
        }

        //ກວດສອບເບີໂທວ່າຖືກຕາມຮູບແບບ ຫຼື ບໍ່
        if (Validation.telephoneValidation(txtTelephone.getText())) {
            JOptionPane.showMessageDialog(this, "ກະລຸນາປ້ອນເບີໂທລະສັບໃຫ້ຖືກຕ້ອງດ້ວຍ", "ຜິດພາດ", JOptionPane.ERROR_MESSAGE);
            txtTelephone.requestFocus();
            return;
        }

        if (txtPassword.getText().isEmpty()) {
            addPassword = oldpassword;
        } else {
            addPassword = PasswordHashing.doHashing(txtPassword.getText());
        }
        try{
            String sql = " select count(*) from employee where username != ? and username = ?";
            pst = conn.prepareStatement(sql);
            pst.setString(1, oldUser);
            pst.setString(2, txtUser.getText());
            rs = pst.executeQuery();
            if(rs.next()){
                JOptionPane.showConfirmDialog(this, " this user already use","error",JOptionPane.ERROR_MESSAGE);
                txtUser.requestFocus();
            }
            
        }catch(Exception e){
            JOptionPane.showMessageDialog(this, e);
            return;
        }
        //ສ້າງ ອ໊ອບເຈັດ dateFormate ເພື່ອຈັດຮູບແບບ ວັນເດືອນປີເກີດ ແລະ ວັນເລີ່ມຕົ້ນເຮັດວຽກ
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        //ແກ້ໄຂຂໍ້ມູນລົງຖານຂໍ້ມູນ
        try {
            String sql = "";
            if (ImgPath != null) {
                try {
                    addPicture = new FileInputStream(new File(ImgPath));
                } catch (FileNotFoundException e) {
                    JOptionPane.showMessageDialog(this, e);
                }
                sql = "UPDATE employee SET emp_name=?, emp_lname=?, gender=?, date_of_b=?, village=?, city=?, province=?, "
                        + "tel=?, start_date=?, picture=?, username=?, password=?, status=?  WHERE emp_id=?";
                pst = conn.prepareStatement(sql);
                pst.setString(1, txtFirstname.getText());
                pst.setString(2, txtLastname.getText());
                pst.setString(3, gender.getSelection().getActionCommand());
                pst.setString(4, dateFormat.format(txtDateofBirth.getDate()));
                pst.setString(5, txtVillage.getText());
                pst.setString(6, txtCity.getSelectedItem().toString());
                pst.setString(7, txtProvince.getSelectedItem().toString());
                pst.setString(8, txtTelephone.getText());
                pst.setString(9, dateFormat.format(txtStartwork.getDate()));
                pst.setBlob(10, addPicture);
                pst.setString(11, txtUser.getText());
                pst.setString(12, addPassword);
                pst.setString(13, txtStatus.getSelectedItem().toString());
                pst.setString(14, txtid.getText());
            } else {
                sql = "UPDATE employee SET emp_name=?, emp_lname=?, gender=?, date_of_b=?, village=?, city=?, province=?, "
                        + "tel=?, start_date=?, username=?, password=?, status=? WHERE emp_id=?";
                pst = conn.prepareStatement(sql);
                pst.setString(1, txtFirstname.getText());
                pst.setString(2, txtLastname.getText());
                pst.setString(3, gender.getSelection().getActionCommand());
                pst.setString(4, dateFormat.format(txtDateofBirth.getDate()));
                pst.setString(5, txtVillage.getText());
                pst.setString(6, txtCity.getSelectedItem().toString());
                pst.setString(7, txtProvince.getSelectedItem().toString());
                pst.setString(8, txtTelephone.getText());
                pst.setString(9, dateFormat.format(txtStartwork.getDate()));
                pst.setString(10, txtUser.getText());
                pst.setString(11, addPassword);
                pst.setString(12, txtStatus.getSelectedItem().toString());
                pst.setString(13, txtid.getText());
            }

            if (pst.executeUpdate() > 0) {
                JOptionPane.showMessageDialog(this, "ປັບປຸງຂໍ້ມູນໃນຖານຂໍ້ມູນສໍາເລັດ", "ສໍາເລັດ", JOptionPane.WIDTH, new FlatSVGIcon("image_svg/check.svg"));
                clearForm();
                tableUpdate();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
        }

    }//GEN-LAST:event_btnEditActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
       int data;
        data = JOptionPane.showConfirmDialog(null, "ທ່ານຕ້ອງການລົບຂໍ້ມູນນີ້ແທ້ ຫຼື ບໍ່?","ຢືນຢັັນ",
        JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if(data != 0){
            clearForm();
            return;
        }
        try{
            String sql = "DELETE FROM employee WHERE emp_id=?";
            pst = conn.prepareStatement(sql);
            pst.setString(1, txtid.getText());
            pst.executeUpdate();
            clearForm();
            tableUpdate();
        }catch(Exception e){
            JOptionPane.showConfirmDialog(null, e);
        }
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void txtFemaleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFemaleActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFemaleActionPerformed

    private void txtProvinceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtProvinceActionPerformed
        String province = txtProvince.getSelectedItem().toString();
            try {

                String sql = "SELECT district_name FROM district WHERE province_id = "
                        + "(SELECT province_id FROM province WHERE province_name = ?)";
                pst = conn.prepareStatement(sql);
                pst.setString(1, province);
                rs = pst.executeQuery();
                txtCity.removeAllItems();
                while (rs.next()) {
                    txtCity.addItem(rs.getString("district_name").trim());
                }
                listprovince();
                txtProvince.setSelectedItem(province);

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, e);
            }
    }//GEN-LAST:event_txtProvinceActionPerformed

    private void txtCityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCityActionPerformed
//        String province = txtProvince.getSelectedItem().toString();
//            try {
//
//                String sql = "SELECT district_name FROM district WHERE province_id = "
//                        + "(SELECT province_id FROM province WHERE province_name = ?)";
//                pst = conn.prepareStatement(sql);
//                pst.setString(1, province);
//                rs = pst.executeQuery();
//                txtCity.removeAllItems();
//                while (rs.next()) {
//                    txtCity.addItem(rs.getString("district_name").trim());
//                }
//                listprovince();
//                txtProvince.setSelectedItem(province);
//
//            } catch (SQLException e) {
//                JOptionPane.showMessageDialog(this, e);
//            }
    }//GEN-LAST:event_txtCityActionPerformed

    private void btnBrownImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBrownImageActionPerformed
       JFileChooser file = new JFileChooser();
        file.setCurrentDirectory(new File(System.getProperty("user.home")));
        FileNameExtensionFilter filter = new FileNameExtensionFilter("*.images", "jpg", "png");
        file.addChoosableFileFilter(filter);
        int result = file.showSaveDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = file.getSelectedFile();
            String path = selectedFile.getAbsolutePath();
            picture.setIcon(resizeImage(path, null));
            ImgPath = path;
        } else {
            System.out.println("No file Selected");
        }
    }//GEN-LAST:event_btnBrownImageActionPerformed
    
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnBrownImage;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnEdit;
    private javax.swing.ButtonGroup gender;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel picture;
    private javax.swing.JComboBox<String> txtCity;
    private com.toedter.calendar.JDateChooser txtDateofBirth;
    private javax.swing.JRadioButton txtFemale;
    private javax.swing.JTextField txtFirstname;
    private javax.swing.JTextField txtLastname;
    private javax.swing.JRadioButton txtMale;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JComboBox<String> txtProvince;
    private javax.swing.JTextField txtSearch;
    private com.toedter.calendar.JDateChooser txtStartwork;
    private javax.swing.JComboBox<String> txtStatus;
    private javax.swing.JTextField txtTelephone;
    private javax.swing.JTextField txtUser;
    private javax.swing.JTextField txtVillage;
    private javax.swing.JRadioButton txtanother;
    private javax.swing.JTextField txtid;
    // End of variables declaration//GEN-END:variables
}
