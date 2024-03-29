package it.uniroma3.siw.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.controller.validator.MovieValidator;
import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Image;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.model.Rewiew;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.ArtistRepository;
import it.uniroma3.siw.repository.MovieRepository;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.MovieService;
import it.uniroma3.siw.service.ReviewService;
import it.uniroma3.siw.service.StorageService;

@Controller
public class MovieController {


	@Autowired 
	private MovieRepository movieRepository;
	
	@Autowired 
	private ArtistRepository artistRepository;

	@Autowired 
	private MovieValidator movieValidator;
	
	@Autowired 
	private CredentialsService credentialsService;

	@Autowired
	private MovieService movieService;

	 @Autowired
	 private ReviewService reviewService;

	 @Autowired
	private StorageService storageService;

	/***************************ADMIN**************************************/
	@GetMapping(value="/admin/formNewMovie")
	public String formNewMovie(Model model) {
		model.addAttribute("movie", new Movie());
		return "admin/formNewMovie.html";
	}

	@GetMapping(value="/admin/formUpdateMovie/{id}")
	public String formUpdateMovie(@PathVariable Long id, Model model) {
		Movie movie = movieService.findById(id);
		if(movie.getDirector() != null){
			String directorImage = movie.getDirector().getImage().getImagePath();
			model.addAttribute("directorImage",directorImage);
		}
		model.addAttribute("movie", movie);
		return "admin/formUpdateMovie.html";
	}

	@GetMapping(value="/admin/indexMovie")
	public String indexMovie() {
		return "admin/indexMovie.html";
	}

	@GetMapping(value = "/admin/removeMovie/{movieId}")
	public String removeMovie(@PathVariable Long movieId) {
		movieService.removeMovie(movieId);		
		return "redirect:/admin/manageMovies";
	}

	@GetMapping(value = "/admin/removeReview/{movieId}/{rewiewId}")
	public String removeReview(@PathVariable("movieId") Long movieId, @PathVariable ("rewiewId") Long rewiewId, Model model) {
		movieService.removeReview(rewiewId);
		Movie movie = movieService.findById(movieId);	
		if(movie.getDirector() != null){
			String directorImage = movie.getDirector().getImage().getImagePath();
			model.addAttribute("directorImage",directorImage);
		}
		model.addAttribute("movie", movie);	
		return "admin/formUpdateMovie.html";
	}
	
	@GetMapping(value="/admin/manageMovies")
	public String manageMovies(Model model) {
		model.addAttribute("movies", this.movieRepository.findAll());
		return "admin/manageMovies.html";
	}
	
	@GetMapping(value="/admin/setDirectorToMovie/{directorId}/{movieId}")
	public String setDirectorToMovie(@PathVariable("directorId") Long directorId, @PathVariable ("movieId") Long movieId, Model model) {
		Movie movie = movieService.setDirectorToMovie(directorId, movieId);
		if(movie.getDirector() != null){
			String directorImage = movie.getDirector().getImage().getImagePath();
			model.addAttribute("directorImage",directorImage);
		}	
		model.addAttribute("movie", movie);
		return "admin/formUpdateMovie.html";
	}
	
	
	@GetMapping(value="/admin/addDirector/{id}")
	public String addDirector(@PathVariable Long id, Model model) {
		model.addAttribute("artists", artistRepository.findAll());
		Movie movie = movieService.findById(id);
		model.addAttribute("movie", movie);
		return "admin/directorsToAdd.html";
	}

	@PostMapping("/admin/movie")
	public String newMovie(@Valid @ModelAttribute Movie movie,@RequestParam ("foto") MultipartFile file, BindingResult bindingResult, Model model) throws IOException {
		
		this.movieValidator.validate(movie, bindingResult);
		if (!bindingResult.hasErrors()) {
			if(!file.isEmpty()){
				Image fileName = storageService.uploadImage(file);                
				movie.setImage(fileName);             
			}
			this.movieRepository.save(movie); 
			model.addAttribute("movie", movie);
			model.addAttribute("movies", this.movieRepository.findAll());
			return "redirect:/admin/manageMovies";
		} else {
			return "admin/formNewMovie.html"; 
		}
	}

	@GetMapping("/admin/updateActors/{id}")
	public String updateActors(@PathVariable Long id, Model model) {

		List<Artist> actorsToAdd = this.actorsToAdd(id);
		model.addAttribute("actorsToAdd", actorsToAdd);
		model.addAttribute("movie", this.movieService.findById(id));

		return "admin/actorsToAdd.html";
	}

	@GetMapping(value="/admin/addActorToMovie/{actorId}/{movieId}")
	public String addActorToMovie(@PathVariable Long actorId, @PathVariable Long movieId, Model model) {
		Movie movie = this.movieService.addActorToMovie(movieId, actorId);
		List<Artist> actorsToAdd = actorsToAdd(movieId);
		if(movie.getDirector() != null){
			String directorImage = movie.getDirector().getImage().getImagePath();
			model.addAttribute("directorImage",directorImage);
		}
		model.addAttribute("movie", movie);
		model.addAttribute("actorsToAdd", actorsToAdd);

		return "admin/actorsToAdd.html";
	}
	
	@GetMapping(value="/admin/removeActorFromMovie/{actorId}/{movieId}")
	public String removeActorFromMovie(@PathVariable Long actorId, @PathVariable Long movieId, Model model) {
		Movie movie = movieService.removeActorFromMovie(movieId, actorId);
		List<Artist> actorsToAdd = actorsToAdd(movieId);
		model.addAttribute("movie", movie);
		model.addAttribute("actorsToAdd", actorsToAdd);
		return "admin/actorsToAdd.html";
	}

	private List<Artist> actorsToAdd(Long movieId) {
		List<Artist> actorsToAdd = new ArrayList<>();

		for (Artist a : artistRepository.findActorsNotInMovie(movieId)) {
			actorsToAdd.add(a);
		}
		return actorsToAdd;
	}

	/***************************DEFAULT**************************************/
	
	@GetMapping("/movie/{id}")
	public String getMovie(@PathVariable Long id, Model model) {
		Movie movie = movieService.getMovie(id);
		if(!(SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken)){
			UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
			User currentUser = credentials.getUser();
			boolean HasReviewed = this.reviewService.HasReviewed(currentUser, movie);
			model.addAttribute("hasReviewed", HasReviewed);
		}
		if(movie.getDirector() != null){
			String directorImage = movie.getDirector().getImage().getImagePath();
			model.addAttribute("directorImage",directorImage);
		}
		model.addAttribute("rewiew", new Rewiew());
		model.addAttribute("movie", movie);
		
		return "movie.html";
	}


	@PostMapping("/addRewiew/{id}")
	public String newRewiew(@PathVariable Long id ,@ModelAttribute Rewiew rewiew, Model model, User user) {
		
		UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
		User currentUser = credentials.getUser();

		Movie movie = this.movieService.newReview(id, rewiew, currentUser);

		boolean HasReviewed = this.reviewService.HasReviewed(currentUser, movie);
		if(movie.getDirector() != null){
			String directorImage = movie.getDirector().getImage().getImagePath();
			model.addAttribute("directorImage",directorImage);
		}

		model.addAttribute("rewiew", rewiew);
		model.addAttribute("movie", movie);
		model.addAttribute("hasReviewed", HasReviewed);
		return "movie.html";
	}

	@GetMapping("/movie")
	public String getMovies(Model model) {
		model.addAttribute("movies", this.movieRepository.findAll());
		return "movies.html";
	}
	
	@GetMapping("/formSearchMovies")
	public String formSearchMovies() {
		return "formSearchMovies.html";
	}

	@PostMapping("/foundMoviesByYear")
	public String searchMoviesByYear(Model model, @RequestParam int year) {
		model.addAttribute("movies", this.movieRepository.findByYear(year));
		return "foundMovies.html";
	}

	@PostMapping("/foundMoviesByTitle")
	public String searchMoviesByTitle(Model model, @RequestParam String title) {
		model.addAttribute("movies", this.movieRepository.findByTitle(title));
		return "foundMovies.html";
	}
	
	
}
