package com.example.apptaphoa.views;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.*;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.example.apptaphoa.controller.DoiMatKhauController;

public class QuenMatKhauFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtTaiKhoan;
	private JTextField txtEmail;
	private JTextField txtMatKhauMoi;
	private JTextField txtOTP;
	private DoiMatKhauController updatePassController = new DoiMatKhauController();
	public String otpRandom = String.valueOf((int) (Math.random() * 9000) + 1000);

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					QuenMatKhauFrame frame = new QuenMatKhauFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public QuenMatKhauFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 510, 387);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(248, 249, 250));
		contentPane.setBorder(new EmptyBorder(15, 15, 15, 15));
		setLocationRelativeTo(null);
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblTitle = new JLabel("ĐẶT LẠI MẬT KHẨU");
		lblTitle.setForeground(new Color(13, 110, 253));
		lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
		lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
		lblTitle.setBounds(0, 29, 510, 30);
		contentPane.add(lblTitle);

		JLabel lblSubtitle = new JLabel("Vui lòng nhập đầy đủ thông tin");
		lblSubtitle.setForeground(new Color(73, 80, 87));
		lblSubtitle.setHorizontalAlignment(SwingConstants.CENTER);
		lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		lblSubtitle.setBounds(0, 59, 510, 20);
		contentPane.add(lblSubtitle);
		JLabel lblTaiKhoan = new JLabel("Tên đăng nhập:");
		lblTaiKhoan.setHorizontalAlignment(SwingConstants.RIGHT);
		lblTaiKhoan.setForeground(new Color(73, 80, 87));
		lblTaiKhoan.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		lblTaiKhoan.setBounds(23, 109, 127, 20);
		contentPane.add(lblTaiKhoan);

		txtTaiKhoan = new JTextField();
		txtTaiKhoan.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		txtTaiKhoan.setBackground(new Color(255, 255, 255));
		txtTaiKhoan
				.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(206, 212, 218)),
						BorderFactory.createEmptyBorder(5, 10, 5, 10)));
		txtTaiKhoan.setBounds(160, 104, 310, 30);
		contentPane.add(txtTaiKhoan);
		txtTaiKhoan.setColumns(10);

		JLabel lblEmail = new JLabel("Email:");
		lblEmail.setHorizontalAlignment(SwingConstants.RIGHT);
		lblEmail.setForeground(new Color(73, 80, 87));
		lblEmail.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		lblEmail.setBounds(23, 149, 127, 20);
		contentPane.add(lblEmail);

		txtEmail = new JTextField();
		txtEmail.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		txtEmail.setBackground(new Color(255, 255, 255));
		txtEmail.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(206, 212, 218)),
				BorderFactory.createEmptyBorder(5, 10, 5, 10)));
		txtEmail.setBounds(160, 144, 310, 30);
		contentPane.add(txtEmail);
		txtEmail.setColumns(10);

		JLabel lblOTP = new JLabel("OTP:");
		lblOTP.setHorizontalAlignment(SwingConstants.RIGHT);
		lblOTP.setForeground(new Color(73, 80, 87));
		lblOTP.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		lblOTP.setBounds(23, 191, 127, 20);
		contentPane.add(lblOTP);

		txtOTP = new JTextField();
		txtOTP.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		txtOTP.setColumns(10);
		txtOTP.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(206, 212, 218)),
				BorderFactory.createEmptyBorder(5, 10, 5, 10)));
		txtOTP.setBackground(Color.WHITE);
		txtOTP.setBounds(160, 184, 213, 30);
		contentPane.add(txtOTP);

		JLabel lblMatKhau = new JLabel("Mật khẩu mới:");
		lblMatKhau.setHorizontalAlignment(SwingConstants.RIGHT);
		lblMatKhau.setForeground(new Color(73, 80, 87));
		lblMatKhau.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		lblMatKhau.setBounds(23, 231, 127, 20);
		contentPane.add(lblMatKhau);

		txtMatKhauMoi = new JTextField();
		txtMatKhauMoi.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		txtMatKhauMoi.setColumns(10);
		txtMatKhauMoi
				.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(206, 212, 218)),
						BorderFactory.createEmptyBorder(5, 10, 5, 10)));
		txtMatKhauMoi.setBackground(Color.WHITE);
		txtMatKhauMoi.setBounds(160, 226, 310, 30);
		contentPane.add(txtMatKhauMoi);

		JSeparator separator = new JSeparator();
		separator.setBounds(50, 268, 420, 4);
		contentPane.add(separator);

		JButton btnExit = new JButton("Thoát");
		btnExit.setForeground(new Color(255, 255, 255));
		btnExit.setBackground(new Color(108, 117, 125));
		btnExit.setFont(new Font("Segoe UI", Font.BOLD, 14));
		btnExit.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
		btnExit.setBounds(320, 290, 150, 35);
		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		contentPane.add(btnExit);

		JButton btnGui = new JButton("Gửi");
		btnGui.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				guiOTP();
			}
		});
		btnGui.setForeground(new Color(255, 255, 255));
		btnGui.setBackground(new Color(13, 110, 253));
		btnGui.setFont(new Font("Segoe UI", Font.BOLD, 14));
		btnGui.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
		btnGui.setBounds(385, 186, 85, 30);
		contentPane.add(btnGui);

		JButton btnDoi = new JButton("Đổi");
		btnDoi.setForeground(new Color(255, 255, 255));
		btnDoi.setBackground(new Color(13, 110, 253));
		btnDoi.setFont(new Font("Segoe UI", Font.BOLD, 14));
		btnDoi.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
		btnDoi.setBounds(50, 290, 150, 35);
		btnDoi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doiMatKhau();
			}
		});
		contentPane.add(btnDoi);
	}

	private void guiOTP() {
		String taiKhoan = txtTaiKhoan.getText().trim();
		String email = txtEmail.getText().trim();

		boolean checkEmail = updatePassController.checkEmail(taiKhoan, email);
		System.out.println("Kiem tra: " + checkEmail);

		if (taiKhoan.isEmpty() || email.isEmpty()) {
			JOptionPane.showMessageDialog(null, "Vui lòng nhập tên đăng nhập và email!", "Lỗi",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (checkEmail != false) {
			Properties prop = new Properties();
			prop.put("mail.smtp.auth", "true");
			prop.put("mail.smtp.ssl.enable", "true");
			prop.put("mail.smtp.host", "smtp.gmail.com");
			prop.put("mail.smtp.port", "465");

			String senderEmail = com.example.apptaphoa.utils.Env.require("TAPHOA_MAIL_USER");
			String password = com.example.apptaphoa.utils.Env.require("TAPHOA_MAIL_PASSWORD");
			Session session = Session.getInstance(prop, new Authenticator() {
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(senderEmail, password);
				}
			});

			try {
				Message message = new MimeMessage(session);
				message.setFrom(new InternetAddress(senderEmail));
				message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(txtEmail.getText()));
				message.setSubject("OTP Reset Mật Khẩu");
				message.setHeader("Content-Type", "text/plain; charset=UTF-8");
				message.setText("OTP đặt lại mật khẩu là: " + otpRandom);

				Transport.send(message);
				JOptionPane.showMessageDialog(null, "Đã gửi!");
			} catch (AuthenticationFailedException ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(null, "Không đăng nhập được SMTP (kiểm tra App Password/2FA).", "Lỗi",
						JOptionPane.ERROR_MESSAGE);
			} catch (SendFailedException ex) {
				JOptionPane.showMessageDialog(null, "Email người nhận không hợp lệ hoặc bị chặn.", "Lỗi",
						JOptionPane.ERROR_MESSAGE);
			} catch (MessagingException ex) {
				JOptionPane.showMessageDialog(null, "Gửi mail thất bại: " + ex.getMessage(), "Lỗi",
						JOptionPane.ERROR_MESSAGE);
			}
		} else {
			JOptionPane.showMessageDialog(null, "Tên đăng nhập hoặc email không đúng", "Lỗi",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void doiMatKhau() {
		String taiKhoan = txtTaiKhoan.getText().trim();
		String email = txtEmail.getText().trim();
		String otp = txtOTP.getText();
		String matKhauMoi = txtMatKhauMoi.getText();

		if (taiKhoan.isEmpty() || email.isEmpty() || otp.isEmpty() || matKhauMoi.isEmpty()) {
			JOptionPane.showMessageDialog(null, "Vui lòng nhập đầy đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
			return;
		}

		boolean checkEmail = updatePassController.checkEmail(taiKhoan, email);
		if (checkEmail != false) {
			if (otp.equals(otpRandom)) {
				updatePassController.resetPass(taiKhoan, matKhauMoi, email);
				JOptionPane.showMessageDialog(null, "Đổi mật khẩu thành công", "Thông báo",
						JOptionPane.INFORMATION_MESSAGE);
				new LoginFrame().setVisible(true);
				dispose();
			} else {
				JOptionPane.showMessageDialog(null, "OTP không đúng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
			}
		} else {
			JOptionPane.showMessageDialog(null, "Tên đăng nhập hoặc mật khẩu không đúng", "Lỗi",
					JOptionPane.ERROR_MESSAGE);
		}
	}
}
