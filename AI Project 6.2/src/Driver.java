//Name: Linh Lam
//Project 6 - COMPUTER VISION
//Professor Scott Thede
//May 9th, 2018

//Main class: This program should do the following:
//		Perform an average filter on the image, then save the result as “average.pgm”
//		Perform a median filter on the image, then save the result as “median.pgm”
//	    Perform edge detection on the image, then save the result as “edge.pgm”
//	    Perform the Hough transform for line detection, then save the result as “lines.pgm”
//	    Perform the Hough transform for circle detection, then save the result as “circles.pgm”


import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Scanner;



public class Driver 
{
	private static int WIDTH = 128;
	private static int HEIGHT = 128;
	private static int MAX_PIXEL = 255;
	private static int accumulator_maxX = 128;
	private static int accumulator_maxY = 128;	
	private static int [][][] possibleR = new int [255][accumulator_maxX][accumulator_maxY];
	private static int[][] orig_pixels;
	private static int[][] pixels;
	private static double[][] epixels;
	private static int [][] accumulator = new int [accumulator_maxX][accumulator_maxY];
	private static int [][] accumulator1 = new int [accumulator_maxX][accumulator_maxY];




	//private static double[][] lpixels;
	
	public static void writeFile( String file ) throws Exception
	{
		BufferedOutputStream writer = new BufferedOutputStream( new FileOutputStream( file ) );
		
		writer.write( 'P' );
		writer.write( '5' );
		writer.write( ' ' );
		writer.write( '1' );
		writer.write( '2' );
		writer.write( '8' );
		writer.write( ' ' );
		writer.write( '1' );
		writer.write( '2' );
		writer.write( '8' );
		writer.write( ' ' );
		writer.write( '2' );
		writer.write( '5' );
		writer.write( '5' );
		writer.write( ' ' );

		for ( int i = 0; i < WIDTH; i++ )
		{
			for ( int j = 0; j < HEIGHT; j++ )
			{
				writer.write( pixels[i][j] );
			}
		}
		
		writer.close();
	}
	
	
	public static void readFile( String file ) throws Exception
	{
		String format;
		int width, height, maxPixel;
		
		Scanner get = new Scanner( new FileReader( file ) );
		
		format = get.next();
		width = get.nextInt();
		height = get.nextInt();
		maxPixel = get.nextInt();
		
		if ( ( width != WIDTH ) || ( height != HEIGHT ) || ( maxPixel != MAX_PIXEL ) )
		{
			System.out.println( "Error in file format. Exiting..." );
			System.exit( 1 );
		}
			
		if ( format.equals("P2") )
		{
			for ( int i = 0; i < WIDTH; i++ )
			{
				for ( int j = 0; j < HEIGHT; j++ )
				{
					orig_pixels[i][j] = get.nextByte( );
				}
			}
		}
		
		if ( format.equals( "P5" ) )
		{
			get.close();
			
			DataInputStream input = new DataInputStream( new FileInputStream( file ) );

			for ( int i = 0; i < 15; i++ )
				input.readUnsignedByte();
			
			for ( int i = 0; i < WIDTH; i++ )
			{
				for ( int j = 0; j < HEIGHT; j++ )
				{
					orig_pixels[i][j] = input.readUnsignedByte();
				}
			}
			
			input.close();
		}
		
	}
	
	//detect best vertical lines
	public static void drawVerticalLine(){
		int max = accumulator [0][0];
		int maxM = 0;
		int maxB = 0;
		for (int m = 0; m < accumulator_maxX ; m++)
			for ( int b = 0; b < accumulator_maxY; b++ ) 
				if (accumulator [m][b]>max){
						max = accumulator [m][b];
						maxM = m;
						maxB = b;
				}
		for (int x = 0; x < WIDTH ; x++)
			for ( int y = 0; y < HEIGHT; y++ ) 
				if (y == maxM*x + maxB){
					pixels[x][y] = 255;
				}
		accumulator[maxM][maxB] = 0;
		
		}
	
	//detect best lines
	public static void drawLine(){
		int max = accumulator1 [0][0];
		int maxM = 0;
		int maxB = 0;
		for (int m = 0; m < accumulator_maxX ; m++)
			for ( int b = 0; b < accumulator_maxY; b++ ) 
				if (accumulator1 [m][b]>max){
						max = accumulator1 [m][b];
						maxM = m;
						maxB = b;
				}
		for (int x = 0; x < WIDTH ; x++)
			for ( int y = 0; y < HEIGHT; y++ ) 
				if (x == maxM*y + maxB){
					pixels[x][y] = 255;
				}
		accumulator1[maxM][maxB] = 0;
		
		}
	
	//detect best circles
	public static void drawCircle(){
		int max = possibleR[0] [0][0];
		int maxX0 = 0;
		int maxY0 = 0;
		int maxR = 0;
		for(int r = 8; r<127; r++) {
			for (int m = 0; m < accumulator_maxX ; m++)
				for ( int b = 0; b < accumulator_maxY; b++ ) 
					if (possibleR[r] [m][b] > max){
							max = possibleR[r] [m][b];
							maxX0 = m;
							maxY0 = b;
							maxR = r;
							//System.out.println(r + " " + m + " " + b);
					}
		}
		//System.out.println(maxX0);
		//System.out.println(maxY0);
		//System.out.println(maxR);
			for (int x = 0; x < WIDTH ; x++)
				for ( int y = 0; y < HEIGHT; y++ ) 
					if (maxR == (int)(Math.sqrt((x-maxX0)*(x-maxX0) + (y-maxY0)*(y-maxY0)))){
						pixels[x][y] = 150;
					}
			possibleR[maxR] [maxX0][maxY0] = 0;
	}


	public static void main( String[] args ) throws Exception
	{
		orig_pixels = new int[WIDTH][HEIGHT];
		pixels = new int[WIDTH][HEIGHT];
		epixels = new double[WIDTH][HEIGHT];

		
		Scanner in = new Scanner( System.in );
		
		System.out.println( "Enter a file name: ");
		String file = in.nextLine();
		in.close();
		
		try
		{
			readFile( file );
		}
		catch( FileNotFoundException e )
		{
			System.out.println( "That file could not be found." );
			System.exit( 1 );
		}
		
		//average filtering
		for ( int i = 1; i < WIDTH-1; i++ )
			for ( int j = 1; j < HEIGHT-1; j++ )
				pixels[i][j] = (orig_pixels[i-1][j-1] + orig_pixels[i-1][j] + orig_pixels[i-1][j+1]
								+ orig_pixels[i][j-1] + orig_pixels[i][j] + orig_pixels[i][j+1]
								+ orig_pixels[i+1][j-1] + orig_pixels[i+1][j] + orig_pixels[i+1][j+1])/9;
		
		for ( int j = 0; j < HEIGHT; j++ )
		{
			pixels[0][j] = 255;
			pixels[WIDTH-1][j] = 255;
		}
		
		for ( int i = 0; i < WIDTH; i++ )
		{
			pixels[i][0] = 255;
			pixels[i][HEIGHT-1] = 255;
		}
			
		writeFile( "average.pgm" );
		
		//median filtering
		int [] array = new int [9];
		for ( int i = 1; i < WIDTH-1; i++ )
			for ( int j = 1; j < HEIGHT-1; j++ ) {
				array[0] = orig_pixels[i-1][j-1];
				array[1] = orig_pixels[i-1][j];
				array[2] = orig_pixels[i-1][j+1];
				array[3] = orig_pixels[i][j-1];
				array[4] = orig_pixels[i][j];
				array[5] = orig_pixels[i][j+1];
				array[6] = orig_pixels[i+1][j-1]; 
				array[7] = orig_pixels[i+1][j];
				array[8] = orig_pixels[i+1][j+1];
				
				Arrays.sort(array);
				int median;
				median = (int) array[4];
				pixels[i][j] = median;
			}
		
		for ( int j = 0; j < HEIGHT; j++ )
		{
			pixels[0][j] = 255;
			pixels[WIDTH-1][j] = 255;
		}
		
		for ( int i = 0; i < WIDTH; i++ )
		{
			pixels[i][0] = 255;
			pixels[i][HEIGHT-1] = 255;
		}
		
		writeFile( "median.pgm" );
		
		//edge detection
		int [][] gx = new int[WIDTH][HEIGHT]; //delta x
		int [][] gy = new int[WIDTH][HEIGHT]; //delta y
		for (int i = 0; i < WIDTH ; i++)
			for ( int j = 0; j < HEIGHT; j++ ) {
				if (i==0 || j==0 || i == WIDTH -1 || j == HEIGHT -1)
				{
					gx[i][j]=gy[i][j]=0;
					epixels[i][j] = 0;
				}
				else
				{
					gx[i][j] = orig_pixels[i-1][j+1] + orig_pixels[i][j+1] + orig_pixels[i+1][j+1]
								- orig_pixels[i-1][j-1] - orig_pixels[i][j-1] - orig_pixels[i+1][j-1];
					gy[i][j] = orig_pixels[i-1][j-1] + orig_pixels[i-1][j] + orig_pixels[i-1][j+1]
								- orig_pixels[i+1][j-1] - orig_pixels[i+1][j] - orig_pixels[i+1][j+1];
					epixels[i][j] = Math.sqrt(gx[i][j] * gx[i][j] + gy[i][j] * gy[i][j]);
				}
				
			}
		
		double sum1 = 0;
		for (int i = 0; i < WIDTH ; i++)
			for ( int j = 0; j < HEIGHT; j++ ) 
				sum1 = sum1 + epixels[i][j];
		double avg = sum1 / (WIDTH * HEIGHT);
		
		
		for (int i = 0; i < WIDTH ; i++)
			for ( int j = 0; j < HEIGHT; j++ ) {
				if (epixels[i][j] > 3*avg)
				{
					pixels[i][j]= 255;
				}
				else
				{
					pixels[i][j]= 0;
				}
			}
		writeFile( "edge.pgm" );
		
		int [][] edgeOnly = new int[WIDTH][HEIGHT]; 
		for (int i = 0; i < WIDTH ; i++)
			for ( int j = 0; j < HEIGHT; j++ ) {
				edgeOnly[i][j] = pixels[i][j];
			}
		
		
		
		//Hough transform for line detection
		for (int x = 0; x < accumulator_maxX ; x++)
			for ( int y = 0; y < accumulator_maxY; y++ ) 
				accumulator[x][y] = 0;

			
		for (int x = 0; x < accumulator_maxX ; x++)
			for ( int y = 0; y < accumulator_maxY; y++ ) 
				accumulator1[x][y] = 0;
		
		
		
		for (int x = 0; x < WIDTH ; x++)
			for ( int y = 0; y < HEIGHT; y++ ) 
				if (pixels[x][y] == 255){
					for (int m = 0; m < accumulator_maxX ; m++)
						for ( int b = 0; b < accumulator_maxY; b++ ) 
							if (b == y - m*x )
								accumulator[m][b]+=1;	
				}
		
		
		for (int x = 0; x < WIDTH ; x++)
			for ( int y = 0; y < HEIGHT; y++ ) 
				if (pixels[x][y] == 255){
					for (int m = 0; m < accumulator_maxX ; m++)
						for ( int b = 0; b < accumulator_maxY; b++ ) 
							if (b == x - m*y )
								accumulator1[m][b]+=1;	
				}
		
		//Circle detection	
		
		for (int r = 0; r<127; r++) {
			int [][] circle = new int [accumulator_maxX][accumulator_maxY];
			for (int x = 0; x < accumulator_maxX ; x++)
				for ( int y = 0; y < accumulator_maxY; y++ ) 
					circle[x][y] = 0;

			
			for (int x = 0; x < WIDTH ; x++)
				for ( int y = 0; y < HEIGHT; y++ ) 
					if (edgeOnly[x][y] == 255){
						for (int x0 = 0; x0 < accumulator_maxX ; x0++)
							for ( int y0 = 0; y0 < accumulator_maxY; y0++ ) 
								if (r == (Math.sqrt((x-x0)*(x-x0) + (y-y0)*(y-y0)))) {
									circle[x0][y0]+=1;
								}
		}
		possibleR[r] = circle;
}


		
		
		
		
		
		drawVerticalLine();
		drawVerticalLine();
		drawVerticalLine();
		
		drawLine();
		drawLine();
		drawLine();
		
		
	
		writeFile( "lines.pgm" );
		
		pixels = edgeOnly;
		drawCircle();
		drawCircle();
		drawCircle();
		drawCircle();
		drawCircle();

		
		writeFile ("circles.pgm");
		
		System.out.println("Average filter - DONE - check file average.pgm");
		System.out.println("Median filter - DONE - check file median.pgm");
		System.out.println("Edge detection - DONE - check file edge.pgm");
		System.out.println("Line detection - DONE - check file lines.pgm");
		System.out.println("Circle detection - DONE - check file circles.pgm");
	}
}
