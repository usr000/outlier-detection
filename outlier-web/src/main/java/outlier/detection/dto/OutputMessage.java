package outlier.detection.dto;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFormat;

@Document(collection ="output")
public class OutputMessage {
	@Id private String id;
	
	private String publisher;
	
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss.SSS", timezone="UTC")
	private Date time;
	
	private List<Double> readings;
	
	private List<Outlier> outliers;
	
	private Stats stats;
	
	public OutputMessage() {
	}
	
//	public OutputMessage(String publisher, Date time, List<Double> readings,
//			List<Outlier> outliers, Stats stats) {
//		super();
//		this.publisher = publisher;
//		this.time = time;
//		this.readings = readings;
//		this.outliers = outliers;
//		this.stats = stats;
//	}

	public String getPublisher() {
		return publisher;
	}
	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	public List<Double> getReadings() {
		return readings;
	}
	public void setReadings(List<Double> readings) {
		this.readings = readings;
	}
	public List<Outlier> getOutliers() {
		return outliers;
	}
	public void setOutliers(List<Outlier> outliers) {
		this.outliers = outliers;
	}
	public Stats getStats() {
		return stats;
	}
	public void setStats(Stats stats) {
		this.stats = stats;
	}
		
}
