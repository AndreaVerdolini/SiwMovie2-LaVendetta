package it.uniroma3.siw.model;

import java.sql.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

@Entity
public class Artist {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

	@NotNull
	private String name;

	@NotNull
	private String surname;
		
	private Date dateOfBirth;
	
	@ManyToMany(mappedBy="actors")
	private Set<Movie> starredMovies;
	
	@OneToMany(mappedBy="director")
	private List<Movie> directedMovies;

	@ManyToOne(fetch = FetchType.EAGER)
	private Image image;
	
	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public Artist(){
		this.starredMovies = new HashSet<>();
		this.directedMovies = new LinkedList<>();
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getSurname() {
		return surname;
	}
	
	public void setSurname(String surname) {
		this.surname = surname;
	}
	
	public Date getDateOfBirth() {
		return dateOfBirth;
	}
	
	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	
	
	
	public Set<Movie> getActorOf() {
		return starredMovies;
	}

	public void setActorOf(Set<Movie> starredMovies) {
		this.starredMovies = starredMovies;
	}

	public List<Movie> getDirectorOf() {
		return directedMovies;
	}

	public void setDirectorOf(List<Movie> directedMovies) {
		this.directedMovies = directedMovies;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, surname);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Artist other = (Artist) obj;
		return Objects.equals(name, other.name) && Objects.equals(surname, other.surname);
	}

}
