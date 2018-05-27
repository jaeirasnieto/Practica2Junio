//1 IMPORTS LIBRERÃ�AS
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

//2 CLASE PRINCIPAL
@WebServlet("/SINT66P2")
public class SINT66P2 extends HttpServlet {
	private final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
	private final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
	private final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
	private final String XML_PrimerDoc = "mml2001.xml";
	private final String URL_generic = "http://gssi.det.uvigo.es/users/agil/public_html/SINT/17-18/";
	private final String MySchema = "mml.xsd";
	private String URL_elem = null;
	
	private String rutaSchema = null;
	private String erroresVinculados= null;
	private boolean invalid = false;
	private Map<String, Document> arboles = new HashMap<String, Document>();
	private List<String> aniosQueFaltan=new ArrayList<String>();
	private List<String> aniosLeidos=new ArrayList<String>();
	private List<String> malFormados = new ArrayList<String>();
	private List<String> invalidos = new ArrayList<String>();
	private List<Xml> listaErrores= new ArrayList<Xml>();
	private Xml er=new Xml();
	
	//2.01 CLASE XML PARA LOS ERRORES DE LOS FICHEROS
	class Xml{ 
		private String url;
		private List<String> error;
		private List<String> fatalError;
		private List<String> warning;
		private boolean e; //true si hay errores
		private boolean f; //true si hay fatal errors
		private boolean w; //true si hay warnings
		public boolean isE() {
			return e;
		}
		public void setE(boolean e) {
			this.e = e;
		}
		public boolean isF() {
			return f;
		}
		public void setF(boolean f) {
			this.f = f;
		}
		public boolean isW() {
			return w;
		}
		public void setW(boolean w) {
			this.w = w;
		}
		public String getUrl() {
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
		}
		public List<String> getError() {
			return error;
		}
		public void setError(List<String> error) {
			this.error = error;
		}
		public List<String> getFatalError() {
			return fatalError;
		}
		public void setFatalError(List<String> fatalError) {
			this.fatalError = fatalError;
		}
		public List<String> getWarning() {
			return warning;
		}
		public void setWarning(List<String> warning) {
			this.warning = warning;
		}
	}
	
	//2.02 CLASE IDIOMA PARA RECOGER LOS IDIOMAS
	static class Idioma implements Comparable<Idioma>{
		private String plang;
		
		public String getLangs() {
			return plang;
		}
		public void setLangs(String plang){
			this.plang = plang;
		}
		@Override
		public int compareTo(Idioma o) {
			// TODO Auto-generated method stub
			int resultado=0;
			if(this.getLangs().compareToIgnoreCase(o.getLangs())<0){
				resultado=1;
			}else{
				resultado=-1;
			}
			return resultado;
		}
		
		
	}
	
	//2.03 CLASE TITULO PARA ALMACENAR LOS TITULOS CON SUS IPS
	class Titulo implements Comparable<Titulo>{ //clase para almacenar los distintos titulos de peliculas con sus oscar
		private String titulo;
		private String ip;
		public String getTitulo() {
			return titulo;
		}
		public void setTitulo(String titulo) {
			this.titulo = titulo;
		}
		public String getIp() {
			return ip;
		}
		public void setIp(String ip) {
			this.ip = ip;
		}
		@Override
		public int compareTo(Titulo o) {
			int resultado=0;

		if(this.getIp()==null & o.getIp()==null){
			if(this.getTitulo().compareToIgnoreCase(o.getTitulo())<0){
				resultado=-1;
			}else resultado=1;
		}else if(this.getIp()==null) resultado=-1;
		else if(o.getIp()==null) resultado=1;
			
		return resultado;
		}
	}
			
	//2.04 CLASE ACTOR PARA ALMACENAR LOS DATOS DE LOS ACTORES
	class Actor implements Comparable<Actor>{ //clase que permite almacenar caracteristicas de actores
		private String nombre;
		private String nacionalidad;
		private String oscar;
		
		public String getNombre() {
			return nombre;
		}
		public void setNombre(String nombre) {
			this.nombre = nombre;
		}
		public String getNacionalidad() {
			return nacionalidad;
		}
		public void setNacionalidad(String nacionalidad) {
			this.nacionalidad = nacionalidad;
		}
		
		@Override
		public int compareTo(Actor o) {
			int resultado=0;
			if(this.getNombre().compareToIgnoreCase(o.getNombre())<0){
				resultado=-1;
			}else{
				resultado=1;
			}
			return resultado;
		}
		public String getOscar() {
			return oscar;
		}
		public void setOscar(String oscar) {
			this.oscar = oscar;
		}
	}
	
	//2.05 CLASE PAIS PARA ALMACENAR LAS PELICULAS DE UN ACTOR
		class Pais {//implements Comparable<pais>{ 
			public String titulo;
			public String lang;
			public String getTitulo() {
				return titulo;
			}
			public void setTitulo(String titulo) {
				this.titulo = titulo;
			}
			public String getLang() {
				return lang;
			}
			public void setLang(String lang) {
				this.lang = lang;
			}
			/*public int compareTo(pais p) {
				int resultado=0;
		        if(this.lang<p.getLang()){
		        	resultado=-1;
		        }else if(this.lang>p.getLang()){
		        	resultado=1;
		        }else{
		        	if(this.getTitulo().compareToIgnoreCase(p.getTitulo())<0){
		        		resultado=1;
		        	}else resultado=-1;
		        }
				return resultado;
		    }*/
		}
	
	//2.06 CLASE PELICULA PARA ALMACENAR LAS PELICULAS DE UN ACTOR
		class Pelicula{
			private List<Titulo> listado;
		
			public List<Titulo> getListado() {
				return listado;
			}
			public void setListado(List<Titulo> listado) {
				this.listado = listado;
			}
		}
		
	//2.07 CLASE PARA LA GESTION DE ERRORES
	class MiClaseErrores implements ErrorHandler {
		public void error(org.xml.sax.SAXParseException exception) throws SAXException {
			 invalid=true;
			 er.setE(true);
			 er.setError(new ArrayList<String>());
			 er.getError().add(exception.getMessage());
		}
		public void fatalError(org.xml.sax.SAXParseException exception) throws SAXException {
			 invalid=true;
			 er.setF(true);
			 er.setFatalError(new ArrayList<String>());
			 er.getFatalError().add(exception.getMessage());
		}
		public void warning(org.xml.sax.SAXParseException exception) throws SAXException {
			 invalid=false;
			 er.setW(true);
			 er.setWarning(new ArrayList<String>());
			 er.getWarning().add(exception.getMessage());
		}
	}
	
	//2.08 CREACION DEL DOCUMENTO
	public Document creaDoc(String url) throws  MalformedURLException, IOException, ParserConfigurationException{
		URL_elem=URL_generic+url.trim();
		Document document = null;
		invalid=false;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(true);
		factory.setNamespaceAware(true);
		try{
			factory.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
			factory.setAttribute(JAXP_SCHEMA_SOURCE, rutaSchema);
			DocumentBuilder builder = factory.newDocumentBuilder();
			builder.setErrorHandler(new MiClaseErrores());
			document = builder.parse(new URL(URL_elem).openStream());  //lectura de docs
		}catch(SAXException e){  //capturamos los malformados
			malFormados.add(url);
		}
		if(invalid){  //capturamos los invalids
			invalidos.add(url);
			er.setUrl(URL_elem);
			listaErrores.add(er);
			er=new Xml();
		}else{   //se anaden los correctos aun mapa de docs
			arboles.put(url, document);
		}
		return document;
	}
	
	//2.09 ARBOL DOM
	public void arbolDom(String cadena) {
		Document doc;
		try {
			doc = creaDoc(cadena);
			NodeList MML=doc.getElementsByTagName("MML");
			for(int i=0;i<MML.getLength();i++){
				if(!aniosQueFaltan.contains(MML.item(i).getTextContent())){  //anhado a una lista todos los documentos con intencion de leer, y si ya estan se descartan
					aniosQueFaltan.add(MML.item(i).getTextContent());
				}
			}
			for(int i=0;i<aniosQueFaltan.size();i++){   //recorro los anios que faltan, y si alguno no esta leido lo parseo recursivamente
				if(!aniosLeidos.contains(aniosQueFaltan.get(i))){
					aniosLeidos.add(aniosQueFaltan.get(i));
					arbolDom(aniosQueFaltan.get(i));
				}
			}
		} catch (IOException | ParserConfigurationException  | NullPointerException e) {  //para cuando un documento no tiene campo mml y por tanto no tiene anios hijos
			for(int i=0;i<aniosQueFaltan.size();i++){
				if(!aniosLeidos.contains(aniosQueFaltan.get(i))){
					arbolDom(aniosQueFaltan.get(i));
				}
			}
		}
	}
	
	//2.10 INIT PARA INICIAR EL SERVLET
	public void init(ServletConfig conf) {
		ServletContext sc = (ServletContext)conf.getServletContext();
		rutaSchema = sc.getRealPath(MySchema);
		arbolDom(XML_PrimerDoc);
		//arboles.forEach((k,v) -> System.out.println("Key: " + k)); Para recorrer el HashMap
	}
	
	//2.11 DO GET
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String p=request.getParameter("p");
		String auto=request.getParameter("auto");
		String pfase=request.getParameter("pfase"); 
		//String errores=request.getParameter("errores");
		PrintWriter out = response.getWriter();
		
		if(p==null){
			if(auto==null){
				pantallaErrorNoPassword(request,response);
			}else if(auto.equals("si")) {
				pantallaErrorAutoNoPassword(request,response);
			}
		}else if(p.equals("contrasena")){ //LA CONTRASENA
			if(auto==null){
				if(pfase==null){
					pantallaInicio(request,response);
				}else if(pfase.equals("02"))pantallaErroresNavegador(request,response,pfase);
				else enviarPantallas(request,response,pfase); 
			}else if(auto.equals("si")){
				if(pfase==null){
					pantallaInicioAuto(request,response);
				}else if(pfase.equals("02"))pantallaErroresAuto(request,response,pfase);
				else pantallasAuto(request,response,pfase);	 
			}
		}else{
			if(auto==null){
				pantallaErrorBadPassword(request,response);
			}else if(auto.equals("si")){
				pantallaErrorAutoBadPassword(request,response);
			}
		}
	}
	
	//2.12 ERROR MODO AUTO NO HAY CONTRASENA
	public void pantallaErrorAutoNoPassword(HttpServletRequest request, HttpServletResponse response) throws IOException{
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/xml");
		PrintWriter out = response.getWriter();
		out.println("<?xml version='1.0' encoding='utf-8' ?>");
		out.println("<wrongRequest>no passwd</wrongRequest>");
	} 
	
	//2.13 ERROR MODO AUTO CONTRASENA INCORRECTA
	public void pantallaErrorAutoBadPassword(HttpServletRequest request, HttpServletResponse response) throws IOException{ //pantalla de errores en modo auto
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/xml");
		PrintWriter out = response.getWriter();
		out.println("<?xml version='1.0' encoding='utf-8' ?>");
		out.println("<wrongRequest>bad passwd</wrongRequest>");
	}
	
	//2.14 PANTALLA ERRORES FASE 02 AUTO
	public void pantallaErroresAuto(HttpServletRequest request, HttpServletResponse response, String pfase) throws IOException{ 
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/xml");
		PrintWriter out = response.getWriter();
        out.println("<?xml version='1.0' encoding='utf-8' ?>");
        out.println("<errores>");
        out.println("<warnings>");
        for(int i=0;i<listaErrores.size();i++){
			if(listaErrores.get(i).getWarning()!=null){
				for(int j=0;j<listaErrores.get(i).getWarning().size();j++){
				out.println("<error>");
				out.println("<file>"+listaErrores.get(i).getUrl()+"</file>");
				out.println("<cause>"+listaErrores.get(i).getWarning().get(j)+"</cause>");
				out.println("</error>");
				}
			}
		}
        out.println("</warnings>");
        out.println("<errors>");
        for(int i=0;i<listaErrores.size();i++){
			if(listaErrores.get(i).getError()!=null){
				for(int j=0;j<listaErrores.get(i).getError().size();j++){
					out.println("<error>");
					out.println("<file>"+listaErrores.get(i).getUrl()+"</file>");
					out.println("<cause>"+listaErrores.get(i).getError().get(j)+"</cause>");
					out.println("</error>");
				}
			}
		}
        out.println("</errors>");
        out.println("<fatalerrors>");
        for(int i=0;i<listaErrores.size();i++){
			if(listaErrores.get(i).getFatalError()!=null) {
				for(int j=0;j<listaErrores.get(i).getFatalError().size();j++){
					out.println("<fatalerror>");
					out.println("<file>"+listaErrores.get(i).getUrl()+"</file>");
					out.println("<cause>"+listaErrores.get(i).getFatalError().get(j)+"</cause>");
					out.println("</fatalerror>");
				}
			}
		}
        out.println("</fatalerrors>");
        out.println("</errores>");
	}
	
	//2.15 PANTALLA ERRORES FASE 02 NAVEGADOR
	public void pantallaErroresNavegador(HttpServletRequest request, HttpServletResponse response, String pfase) throws IOException{ //pantalla de errores modo navegador
		int warnings=0,errors=0,fatals=0;
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<!DOCTYPE html>");
		out.println("<html>");
		out.println("<head>");
		out.println("<link rel='stylesheet' href='mml.css'");
		out.println("</head>");
		out.println("<body>");
		out.println("<h1>Servicio de consulta de peliculas</h1>");
		out.println("<form  name='form' action='P2M'>");
		for(int i=0;i<listaErrores.size();i++){ 
			if(listaErrores.get(i).isE()) errors++;
			if(listaErrores.get(i).isF()) fatals++;
			if(listaErrores.get(i).isW()) warnings++;
		}
		out.println("<h2>Se han encontrado "+warnings+" ficheros con warnings:</h2>");
		for(int i=0;i<listaErrores.size();i++){
			if(listaErrores.get(i).getWarning()!=null){
				for(int j=0;j<listaErrores.get(i).getWarning().size();j++){
				out.println(listaErrores.get(i).getUrl()+"---"+listaErrores.get(i).getWarning().get(j)+"<br>");
				}
			}
		}
		out.println("<h2>Se han encontrado "+errors+" ficheros con errores:</h2>");
		for(int i=0;i<listaErrores.size();i++){
			if(listaErrores.get(i).getError()!=null){
				for(int j=0;j<listaErrores.get(i).getError().size();j++){
					out.println(listaErrores.get(i).getUrl()+"---"+listaErrores.get(i).getError().get(j)+"<br>");
				}
			}
		}
		out.println("<h2>Se han encontrado "+fatals+" ficheros con errores fatales:</h2>");
		for(int i=0;i<listaErrores.size();i++){
			if(listaErrores.get(i).getFatalError()!=null) {
				for(int j=0;j<listaErrores.get(i).getFatalError().size();j++){
					out.println(listaErrores.get(i).getUrl()+"---"+listaErrores.get(i).getFatalError().get(j)+"<br>");
				}
			}
		}
		//out.println("<button type='submit' value='atras' onClick='atras()'>AtrÃ¡s</button>");
		out.println("<a href='?p=contrasena&pfase=01'>AtrÃ¡s</a>");
		out.println("</form>");
		out.println("<footer>");
		out.println("<p>Humberto JosÃ© ReimÃºndez MartÃ­nez</p>");
		out.println("</footer>");
		out.println("</body>");
		out.println("</html>");
		return;
	}
	
	//2.16 FASE 01 MODO AUTO
	public void pantallaInicioAuto(HttpServletRequest request, HttpServletResponse response) throws IOException{
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/xml");
		PrintWriter out = response.getWriter();
		out.println("<?xml version='1.0' encoding='utf-8' ?>");
        out.println("<service>");
        out.println("<status>OK</status>");
        out.println("</service>");
	}
	
	//2.17 PANTALLAS FASES MODO AUTO
	public void pantallasAuto(HttpServletRequest request, HttpServletResponse response, String pfase) throws IOException{
		switch(Integer.parseInt(pfase)) {
		case 01:
			pantallaInicioAuto(request,response);
			break;
		case 02:
			pantallaErroresAuto(request,response,pfase);
			break;
		case 21:
			pantalla21Auto(request,response);
			break;
		case 22:
			pantalla22Auto(request,response);
			break;
		case 23:
			pantalla23Auto(request,response);
			break;
		case 24:
			pantalla24Auto(request,response);
			break;
		default:
			pantallaInicioAuto(request,response);
			break;
		}
	}
	
	//2.18 FASE 21 MODO AUTO
	public void pantalla21Auto(HttpServletRequest request, HttpServletResponse response) throws IOException{
		List<Idioma> langs = getC2Langs();
		response.setCharacterEncoding("utf-8");
        response.setContentType("text/xml");
		PrintWriter out = response.getWriter();
		out.println("<?xml version='1.0' encoding='utf-8' ?>");
        out.println("<langs>");
        //out.println("<lang>"+"zulÃº"+"</lang>");
        for(Idioma s:langs){
        	out.println("<lang>"+s+"</lang>");
        }
        out.println("</langs>");
	}
	
	//2.19 FASE 22 MODO AUTO
	public void pantalla22Auto(HttpServletRequest request, HttpServletResponse response) throws IOException{
		List<Actor> ac = getC2Acts(request.getParameter("plang"));
		response.setCharacterEncoding("utf-8");
        response.setContentType("text/xml");
		PrintWriter out = response.getWriter();
		out.println("<?xml version='1.0' encoding='utf-8' ?>");
        out.println("<acts>");
        //out.println("<ac ciudad='Nueva York, EEUU' oscar='true'>Stallone, Sylvester</ac>");
        for(Actor s:ac){
        	out.println("<ac ciudad='"+s.getNacionalidad()+"' oscar='"+s.getOscar()+"'>"+s.getNombre()+"</ac>");
        }
        out.println("</acts>");
	}
	
	//2.20 FASE 23 MODO AUTO
	public void pantalla23Auto(HttpServletRequest request, HttpServletResponse response) throws IOException{
		//ArrayList<actor>a=getC1Actores(request.getParameter("anio"),request.getParameter("pelicula"));
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/xml");
		PrintWriter out = response.getWriter();
        out.println("<?xml version='1.0' encoding='utf-8' ?>");
        out.println("<paises>");
        out.println("<pais lang='en' num='9'>EEUU</pais>");
        /*for(actor s:a){
        	out.println("<act ciudad='"+s.getNacionalidad()+"'>"+s.getNombre()+"</act>");
        }*/
        out.println("</paises>");
	}
	
	//2.21 FASE 24 MODO AUTO
	public void pantalla24Auto(HttpServletRequest request, HttpServletResponse response) throws IOException{
		//filmografia f=getC1Filmografia(request.getParameter("anio"),request.getParameter("pelicula"),request.getParameter("act"));
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/xml");
		PrintWriter out = response.getWriter();
		out.println("<?xml version='1.0' encoding='utf-8' ?>");
        out.println("<titulos>");
        out.println("<titulo ip='HJRM7'>Rocky IV</titulo>");
        /*for(titulo t:f.getLista()){
			if(t.getOscar()==null){
				out.println("<film>"+t.getTitulo()+"</film>");
			}else{
				out.println("<film oscar='"+t.getOscar()+"'>"+t.getTitulo()+"</film>");
			}
		}*/
        out.println("</titulos>");
	}
	
	//2.22 MÃ‰TODO OBLIGATORIO LANGS 
		public List<Idioma> getC2Langs(){
			List<Idioma> langs= new ArrayList<Idioma>();
			boolean noEsta = true;
			String [] idiomas ;
			NodeList pelicula;
			Iterator<Entry<String, Document>> it = arboles.entrySet().iterator();
			while (it.hasNext()) {
			    Entry<String, Document> e = it.next();
			    pelicula = e.getValue().getElementsByTagName("Pelicula");
			    for(int j=0; j<pelicula.getLength();j++) {
			    	for(int i=0; i<pelicula.item(j).getAttributes().getLength();i++) {
			    		if(pelicula.item(j).getAttributes().item(i).getNodeName().equals("langs")) {
			    			idiomas = pelicula.item(j).getAttributes().item(i).getTextContent().split("\\s");
			    			for (String s: idiomas) {
			    				for (Idioma k : langs) {
			    					if(k.getLangs().equals(s)) noEsta = false;
			    				}
			    				if(noEsta) {
			    					Idioma idi = new Idioma();
			    					idi.setLangs(s);
			    					langs.add(idi);			    					
			    				}
		    					noEsta = true;
			    			}			    			
			    		}		    		
			    	}		    	
			    }   
			}
			java.util.Collections.sort(langs);
			return langs;
		}
	
	//2.23 MÃ‰TODO OBLIGATORIO ACTS
		public List<Actor> getC2Acts(String plang){
			Actor a = new Actor();
			a.setOscar("sin Ã³scar");
			boolean x=false;
			List<Actor> act = new ArrayList<Actor>();
			String [] idiomas;
			NodeList pelicula, reparto;
			Iterator<Entry<String, Document>> it = arboles.entrySet().iterator();
			while (it.hasNext()) {
			    Entry<String, Document> e = it.next();
			    pelicula = e.getValue().getElementsByTagName("Pelicula");
			    for(int j=0; j<pelicula.getLength();j++) {
			    	for(int i=0; i<pelicula.item(j).getAttributes().getLength();i++) {
			    		if(pelicula.item(j).getAttributes().item(i).getNodeName().equals("langs")) {
			    			idiomas = pelicula.item(j).getAttributes().item(i).getTextContent().split("\\s");
			    			for (String s: idiomas) {
			    				if(s.equals(plang)) {
			    					for(int k=0; k<pelicula.item(j).getChildNodes().getLength();k++) {
			    						if(pelicula.item(j).getChildNodes().item(k).getNodeName().equals("Reparto")) {
			    							for(int f=0; f<pelicula.item(j).getChildNodes().item(k).getChildNodes().getLength();f++) {
			    								if(pelicula.item(j).getChildNodes().item(k).getChildNodes().item(f).getNodeName().equals("Nombre")) {		    									
			    									x=true;
			    									a.setNombre(pelicula.item(j).getChildNodes().item(k).getChildNodes().item(f).getTextContent());
			    								}
			    								if(pelicula.item(j).getChildNodes().item(k).getChildNodes().item(f).getNodeType()==Node.TEXT_NODE) {
			    									if(pelicula.item(j).getChildNodes().item(k).getChildNodes().item(f).getTextContent().contains(",")) {
			    										x=true;
				    									a.setNacionalidad(pelicula.item(j).getChildNodes().item(k).getChildNodes().item(f).getTextContent());
			    									}		
			    								}
			    								if(pelicula.item(j).getChildNodes().item(k).getChildNodes().item(f).getNodeName().equals("Oscar")) {
			    									x=true;
			    									a.setOscar("con Ã³scar");
			    								}
			    							}
			    							if(x) {
			    								agregaSiProcede(act,a);
			    								a = new Actor();
			    								a.setOscar("sin Ã³scar");
			    								x=false;
			    							}			    								
			    						}
			    					}
			    				}
			    			}
			    		}
			    	}
			    }
			}
			return act;    
		}
		
	private void agregaSiProcede(List<Actor> act, Actor a) {
		boolean meter = false;
		while(!meter) {
			for (Actor t : act) {
				if(t.getNombre().equals(a.getNombre())) {
					if(t.getOscar().equals("sin Ã³scar") && a.getOscar().equals("con Ã³scar")) {
						t=a;
					}
				}
				else meter = true;
			}
		}
	}

	//2.24 MÃ‰TODO OBLIGATORIO PAISES
	
		
	//2.25 MÃ‰TODO OBLIGATORIO PELICULAS
	
		
	//2.26 PANTALLA NAVEGADOR NO HAY CONTRASENA
	public void pantallaErrorNoPassword(HttpServletRequest request, HttpServletResponse response) throws IOException{ 
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<!DOCTYPE html>");
		out.println("<html>");
		out.println("<head>");
		out.println("<link rel='stylesheet' href='mml.css'");
		out.println("</head>");
		out.println("<body>");
		out.println("<h1>Error</h1>");
		out.println("<h2>Introduce la contrasena en la query String</h2>");
		out.println("<footer>");
		out.println("<p>Humberto ReimÃºndez MartÃ­nez</p>");
		out.println("</footer");
		out.println("</body>");
		out.println("</html>");
		return;
	}
	
	//2.27 ERROR NAVEGADOR CONTRASENA INCORRECTA
	public void pantallaErrorBadPassword(HttpServletRequest request, HttpServletResponse response) throws IOException{
		PrintWriter out = response.getWriter();
		response.setCharacterEncoding("utf-8");
    	response.setContentType("text/html");
		out.println("<!DOCTYPE html>");
		out.println("<html>");
		out.println("<body>");
		out.println("<h1>Error</h1>");
		out.println("<h2>Contrasena mal introducida en la query string</h2>");
		out.println("<footer>");
		out.println("<p>Humberto JosÃ© ReimÃºndez MartÃ­nez</p>");
		out.println("</footer");
		out.println("</body>");
		out.println("</html>");
		}
	
	//2.28 ERROR NAVEGADOR Y AUTO FALTA PARÃ�METRO OBLIGATORIO
	
		
	//2.29 PANTALLAS FASES NAVEGADOR
	public void enviarPantallas(HttpServletRequest request, HttpServletResponse response, String pfase) throws IOException {
		switch(Integer.parseInt(pfase)) {
		case 01:
			pantallaInicio(request,response);
			break;
		case 02:
			pantallaErroresNavegador(request,response,pfase);
			break;
		case 21:
			pantalla21(request,response);
			break;
		case 22:
			pantalla22(request,response);
			break;
		case 23:
			pantalla23(request,response);
			break;
		case 24:
			pantalla24(request,response);
			break;
		default:
			pantallaInicio(request,response);
			break;
		}
	}
	
	//2.30 FASE 21 NAVEGADOR
	public void pantalla21(HttpServletRequest request, HttpServletResponse response) throws IOException {
		List<Idioma> langs;
		langs = getC2Langs();
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<!DOCTYPE html>");
		out.println("<html>");
		out.println("<head>");
		out.println("<title>SINTP2</title>");
		out.println("<link rel='stylesheet' href='mml.css'>");
		out.println("<script src='script.js'></script>");
		out.println("</head>");
		out.println("<body>");
		out.println("<h1>Servicio de consulta de pelÃ­culas</h1>"); 
		out.println("<h2>Selecciona un idioma:</h2>");
		out.println("<form  name='form' action='P2M'>");
		//out.println("<input type='radio' name='plang' value='Zulu'>ZulÃº<br><br>");
		for(int i=0;i<langs.size();i++){
			if(i==0) out.println("<input type='radio' name='plang' checked='checked' value='"+langs.get(i).getLangs()+"'>"+langs.get(i).getLangs()+"<br>");
			else out.println("<input type='radio' name='plang' value='"+langs.get(i).getLangs()+"'>"+langs.get(i).getLangs()+"<br>");
		}
		out.println("<input type='hidden' name='pfase' value='21'>");
		out.println("<input type='hidden' name='p' value='contrasena'>");
		out.println("<button type='submit' value='siguiente' onClick='siguiente()'>Enviar</button><br>");
		out.println("<button type='submit' value='atras' onClick='atras()'>AtrÃ¡s</button>");
		out.println("<button type='submit' value='inicio' onClick='inicio()'>Inicio</button>");
		out.println("</form>");
		out.println("<footer>");
		out.println("<p>Humberto JosÃ© ReimÃºndez MartÃ­nez</p>");
		out.println("</footer>");
		out.println("</body>");
		out.println("</html>");
		return;
	}
	
	//2.31 FASE 22 NAVEGADOR
	public void pantalla22(HttpServletRequest request, HttpServletResponse response) throws IOException {
		List<Actor> a=getC2Acts(request.getParameter("plang"));
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<!DOCTYPE html>");
		out.println("<html>");
		out.println("<head>");
		out.println("<link rel='stylesheet' href='mml.css'>");
		out.println("<title>SINTP2</title>");
		out.println("<script src='script.js'></script>");
		out.println("</head>");
		out.println("<body>");
		out.println("<h1>Servicio de consulta de pelÃ­culas</h1>");
		out.println("<h2>Idioma="+request.getParameter("plang")+"</h2>");
		out.println("<h2>Selecciona un Actor/Actriz:</h2>");
		out.println("<form  name='form' action='P2M'>");
		//out.println("<input type='radio' name='pact' value='Stallone, Sylvester'>Stallone, Sylvester (Nueva York, EEUU) -- con Ã“scar<br><br>");
		for(int i=0;i<a.size();i++){
			if(i==0) out.println("<input type='radio' name='pact' checked='checked' value='"+a.get(i).getNombre()+"'>"+a.get(i).getNombre()+" ("+a.get(i).getNacionalidad()+")"+" -- "+a.get(i).getOscar()+"<br>");
			else out.println("<input type='radio' name='pact' checked='checked' value='"+a.get(i).getNombre()+"'>"+a.get(i).getNombre()+" ("+a.get(i).getNacionalidad()+")"+" -- "+a.get(i).getOscar()+"<br>");
		}	
		out.println("<input type='hidden' name='plang' value='"+request.getParameter("plang")+"'>");
		out.println("<input type='hidden' name='pfase' value='22'>"); 
		out.println("<input type='hidden' name='p' value='contrasena'>");
		out.println("<button type='submit' value='siguiente' onClick='siguiente()'>Enviar</button><br>");
		out.println("<button type='submit' value='atras' onClick='atras()'>AtrÃ¡s</button>");
		out.println("<button type='submit' value='inicio' onClick='inicio()'>Inicio</button>");
		out.println("</form>");
		out.println("<footer>");
		out.println("<p>Humberto JosÃ© ReimÃºndez MartÃ­nez</p>");
		out.println("</footer>");
		out.println("</body>");
		out.println("</html>");
		return;
	}	
	
	//2.32 FASE 23 NAVEGADOR
	public void pantalla23(HttpServletRequest request, HttpServletResponse response) throws IOException {
		//ArrayList<pais> pa=getC2Paises(request.getParameter("plang"),request.getParameter("pact"));
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<!DOCTYPE html>");
		out.println("<html>");
		out.println("<head>");
		out.println("<link rel='stylesheet' href='mml.css'>");
		out.println("<title>SINTP2</title>");
		out.println("<script src='script.js'></script>");
		out.println("</head>");
		out.println("<body>");
		out.println("<h1>Servicio de consulta de pelÃ­culas</h1>");
		out.println("<h2>Idioma="+request.getParameter("plang")+", "+"Actor/Actriz="+request.getParameter("pact")+"</h2>");
		out.println("<h2>Selecciona un paÃ­s:</h2>");
		out.println("<form  name='form' action='P2M'>");
		out.println("<input type='radio' name='ppais' value='EEUU'>EEUU (9 pelÃ­culas) -- idioma por defecto='en'<br><br>");
		/*for(int i=0;i<pa.size();i++){
			if(i==0) out.println("<input type='radio' name='act' checked='checked' value='"+pa.get(i).getNombre()+"'>"+pa.get(i).getNombre()+" ("+pa.get(i).getNacionalidad()+")"+"<br>");
			else out.println("<input type='radio' name='act' value='"+pa.get(i).getNombre()+"'>"+pa.get(i).getNombre()+" ("+pa.get(i).getNacionalidad()+")"+"<br>");
		}*/
		out.println("<input type='hidden' name='pact' value='"+request.getParameter("pact")+"'>");
		out.println("<input type='hidden' name='plang' value='"+request.getParameter("plang")+"'>");
		out.println("<input type='hidden' name='pfase' value='23'>");
		out.println("<input type='hidden' name='p' value='contrasena'>");
		out.println("<button type='submit' value='siguiente' onClick='siguiente()'>Enviar</button><br>");
		out.println("<button type='submit' value='atras' onClick='atras()'>AtrÃ¡s</button>");
		out.println("<button type='submit' value='inicio' onClick='inicio()'>Inicio</button>");
		out.println("</form>");
		out.println("<footer>");
		out.println("<p>Humberto JosÃ© ReimÃºndez MartÃ­nez</p>");
		out.println("</footer>");
		out.println("</body>");
		out.println("</html>");
		return;
	}
	
	//2.33 FASE 24 NAVEGADOR
	public void pantalla24(HttpServletRequest request, HttpServletResponse response) throws IOException {
		//peliculas f=getC2Peliculas(request.getParameter("plang"),request.getParameter("pact"),request.getParameter("ppais"));
		//String[] peliculas;
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<!DOCTYPE html>");
		out.println("<html>");
		out.println("<head>");
		out.println("<link rel='stylesheet' href='mml.css'>");
		out.println("<title>SINTP2</title>");
		out.println("<script src='script.js'></script>");
		out.println("</head>");
		out.println("<body>");
		out.println("<h1>Servicio de consulta de pelÃ­culas</h1>");
		out.println("<h2>Idioma="+request.getParameter("plang")+", "+"Actor/Actriz="+request.getParameter("pact")+", "+"Pais="+request.getParameter("ppais")+"</h2>");
		out.println("<h2>Estas son sus pelÃ­culas:</h2>");
		out.println("<form  name='form' action='P2M'>");
		out.println("1.-PelÃ­cula=Rocky IV, IP=HJRM7<br><br>");
		/*for(titulo t:f.getLista()){
				out.println("titulo: "+t.getTitulo()+" ip: "+t.getIp()+"<br>");
		}*/
		out.println("<input type='hidden' name='ppais' value='"+request.getParameter("ppais")+"'>");
		out.println("<input type='hidden' name='pact' value='"+request.getParameter("pact")+"'>");
		out.println("<input type='hidden' name='plang' value='"+request.getParameter("plang")+"'>");
		out.println("<input type='hidden' name='pfase' value='24'>");
		out.println("<input type='hidden' name='p' value='contrasena'>");
		out.println("<button type='submit' value='atras' onClick='atras()'>AtrÃ¡s</button>");
		out.println("<button type='submit' value='inicio' onClick='inicio()'>Inicio</button>");
		out.println("</form>");
		out.println("<footer>");
		out.println("<p>Humberto JosÃ© ReimÃºndez MartÃ­nez</p>");
		out.println("</footer");
		out.println("</body>");
		out.println("</html>");
		return;
	}
	
	//2.34 FASE 01 NAVEGADOR
	public void pantallaInicio(HttpServletRequest request, HttpServletResponse response) throws IOException {
		PrintWriter out = response.getWriter();
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html");
		out.println("<!DOCTYPE html>");
		out.println("<html>");
		out.println("<head>");
		out.println("<title>SINTP2</title>");
		out.println("<link rel='stylesheet' href='mml.css'>");
		out.println("<script src='script.js'></script>");
		out.println("</head>");
		out.println("<body>");
		out.println("<h1>Servicio de consulta de peliculas</h1>");
		out.println("<h1>Bienvenido a este servicio</h1>");
		out.println("<form name='form' action='P2M'>");
		out.println("<a href='P2M?p=contrasena&pfase=02' target='_blank'>Pulse aqui para ver los ficheros errÃ³neos</a><br>");
		out.println("<h2>Selecciona una consulta:</h2>");
		out.println("<input type='radio' name='pfase' value='01' checked='checked'>PelÃ­culas de un actor/actriz, en un idioma, producidas en un paÃ­s<br><br>");
		out.println("<input type='hidden' name='p' value='contrasena'>");
		out.println("<button type='submit' value='Enviar' onClick='siguiente()'>Enviar</button>");
		out.println("</form>");
		out.println("<footer>");
		out.println("<p>Humberto JosÃ© ReimÃºndez MartÃ­nez</p>");
		out.println("</footer>");
		out.println("</body>");
		out.println("</html>");
		return;
	}

}
