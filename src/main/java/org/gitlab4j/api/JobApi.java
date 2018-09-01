package org.gitlab4j.api;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

import javax.ws.rs.core.Form;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.gitlab4j.api.models.ArtifactsFile;
import org.gitlab4j.api.models.Job;

/**
 * This class provides an entry point to all the GitLab API job calls.
 */
public class JobApi extends AbstractApi implements Constants {

    public JobApi(GitLabApi gitLabApi) {
        super(gitLabApi);
    }

    /**
     * Get a list of jobs in a project.
     *
     * GET /projects/:id/jobs
     *
     * @param projectIdOrPath id, path of the project, or a Project instance holding the project ID or path
     * @return a list containing the jobs for the specified project ID
     * @throws GitLabApiException if any exception occurs during execution
     */
    public List<Job> getJobs(Object projectIdOrPath) throws GitLabApiException {
        Response response = get(Response.Status.OK, getDefaultPerPageParam(),
                "projects", getProjectIdOrPath(projectIdOrPath), "jobs");
        return (response.readEntity(new GenericType<List<Job>>() {
        }));
    }

    /**
     * Get a list of jobs in a project in the specified page range.
     *
     * GET /projects/:id/jobs
     *
     * @param projectIdOrPath id, path of the project, or a Project instance holding the project ID or path to get the jobs for
     * @param page the page to get
     * @param perPage the number of Job instances per page
     * @return a list containing the jobs for the specified project ID in the specified page range
     * @throws GitLabApiException if any exception occurs during execution
     */
    public List<Job> getJobs(Object projectIdOrPath, int page, int perPage) throws GitLabApiException {
        Response response = get(Response.Status.OK, getPageQueryParams(page, perPage), "projects", getProjectIdOrPath(projectIdOrPath), "jobs");
        return (response.readEntity(new GenericType<List<Job>>() {
        }));
    }

    /**
     * Get a Pager of jobs in a project.
     *
     * GET /projects/:id/jobs
     *
     * @param projectIdOrPath id, path of the project, or a Project instance holding the project ID or path to get the jobs for
     * @param itemsPerPage the number of Job instances that will be fetched per page
     * @return a Pager containing the jobs for the specified project ID
     * @throws GitLabApiException if any exception occurs during execution
     */
    public Pager<Job> getJobs(Object projectIdOrPath, int itemsPerPage) throws GitLabApiException {
        return (new Pager<Job>(this, Job.class, itemsPerPage, null, "projects", getProjectIdOrPath(projectIdOrPath), "jobs"));
    }

    /**
     * Get a list of jobs in a project.
     *
     * GET /projects/:id/jobs
     *
     * @param projectIdOrPath id, path of the project, or a Project instance holding the project ID or path to get the jobs for
     * @param scope the scope of jobs, one of: CREATED, PENDING, RUNNING, FAILED, SUCCESS, CANCELED, SKIPPED, MANUAL
     * @return a list containing the jobs for the specified project ID
     * @throws GitLabApiException if any exception occurs during execution
     */
    public List<Job> getJobs(Object projectIdOrPath, JobScope scope) throws GitLabApiException {
        GitLabApiForm formData = new GitLabApiForm().withParam("scope", scope).withParam(PER_PAGE_PARAM, getDefaultPerPage());
        Response response = get(Response.Status.OK, formData.asMap(), "projects", getProjectIdOrPath(projectIdOrPath), "jobs");
        return (response.readEntity(new GenericType<List<Job>>() {}));
    }

    /**
     * Get a list of jobs in a pipeline.
     *
     * GET /projects/:id/pipelines/:pipeline_id/jobs
     *
     * @param projectIdOrPath id, path of the project, or a Project instance holding the project ID or path to get the pipelines for
     * @param pipelineId the pipeline ID to get the list of jobs for
     * @return a list containing the jobs for the specified project ID and pipeline ID
     * @throws GitLabApiException if any exception occurs during execution
     */
    public List<Job> getJobsForPipeline(Object projectIdOrPath, int pipelineId) throws GitLabApiException {
        Response response = get(Response.Status.OK, getDefaultPerPageParam(), 
                "projects", getProjectIdOrPath(projectIdOrPath), "pipelines", pipelineId, "jobs");
        return (response.readEntity(new GenericType<List<Job>>() {}));
    }

    /**
     * Get a list of jobs in a pipeline.
     *
     * GET /projects/:id/pipelines/:pipeline_id/jobs
     *
     * @param projectIdOrPath id, path of the project, or a Project instance holding the project ID or path to get the pipelines for
     * @param pipelineId the pipeline ID to get the list of jobs for
     * @param scope the scope of jobs, one of: CREATED, PENDING, RUNNING, FAILED, SUCCESS, CANCELED, SKIPPED, MANUAL
     * @return a list containing the jobs for the specified project ID and pipeline ID
     * @throws GitLabApiException if any exception occurs during execution
     */
    public List<Job> getJobsForPipeline(Object projectIdOrPath, int pipelineId, JobScope scope) throws GitLabApiException {
        GitLabApiForm formData = new GitLabApiForm().withParam("scope", scope).withParam(PER_PAGE_PARAM, getDefaultPerPage());
        Response response = get(Response.Status.OK, formData.asMap(), "projects", getProjectIdOrPath(projectIdOrPath), "pipelines", pipelineId, "jobs");
        return (response.readEntity(new GenericType<List<Job>>() {}));
    }

    /**
     * Get single job in a project.
     *
     * GET /projects/:id/jobs/:job_id
     *
     * @param projectIdOrPath id, path of the project, or a Project instance holding the project ID or path to get the job for
     * @param jobId the job ID to get
     * @return a single job for the specified project ID
     * @throws GitLabApiException if any exception occurs during execution
     */
    public Job getJob(Object projectIdOrPath, int jobId) throws GitLabApiException {
        Response response = get(Response.Status.OK, null, "projects", getProjectIdOrPath(projectIdOrPath), "jobs", jobId);
        return (response.readEntity(Job.class));
    }

    /**
     * Get single job in a project as an Optional instance.
     *
     * GET /projects/:id/jobs/:job_id
     *
     * @param projectIdOrPath id, path of the project, or a Project instance holding the project ID or path to get the job for
     * @param jobId the job ID to get
     * @return a single job for the specified project ID as an Optional intance
     */
    public Optional<Job> getOptionalJob(Object projectIdOrPath, int jobId) {
        try {
            return (Optional.ofNullable(getJob(projectIdOrPath, jobId)));
        } catch (GitLabApiException glae) {
            return (GitLabApi.createOptionalFromException(glae));
        }
    }

    /**
     * Download the artifacts file from the given reference name and job provided the job finished successfully.
     * The file will be saved to the specified directory. If the file already exists in the directory it will
     * be overwritten.
     *
     * GET /projects/:id/jobs/artifacts/:ref_name/download?job=name
     *
     * @param projectIdOrPath id, path of the project, or a Project instance holding the project ID or path
     * @param ref the ref from a repository
     * @param jobName the name of the job to download the artifacts for
     * @param directory the File instance of the directory to save the file to, if null will use "java.io.tmpdir"
     * @return a File instance pointing to the download of the specified artifacts file
     * @throws GitLabApiException if any exception occurs
     */
    public File downloadArtifactsFile(Object projectIdOrPath, String ref, String jobName, File directory) throws GitLabApiException {

        Form formData = new GitLabApiForm().withParam("job", jobName, true);
        Response response = getWithAccepts(Response.Status.OK, formData.asMap(), MediaType.MEDIA_TYPE_WILDCARD,
                "projects", getProjectIdOrPath(projectIdOrPath), "jobs", "artifacts", ref, "download");

        try {

            if (directory == null)
                directory = new File(System.getProperty("java.io.tmpdir"));

            String filename = jobName + "-artifacts.zip";
            File file = new File(directory, filename);

            InputStream in = response.readEntity(InputStream.class);
            Files.copy(in, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return (file);

        } catch (IOException ioe) {
            throw new GitLabApiException(ioe);
        }
    }

    /**
     * Get an InputStream pointing to the artifacts file from the given reference name and job
     * provided the job finished successfully. The file will be saved to the specified directory.
     * If the file already exists in the directory it will be overwritten.
     *
     * GET /projects/:id/jobs/artifacts/:ref_name/download?job=name
     *
     * @param projectIdOrPath id, path of the project, or a Project instance holding the project ID or path
     * @param ref the ref from a repository
     * @param jobName the name of the job to download the artifacts for
     * @return an InputStream to read the specified artifacts file from
     * @throws GitLabApiException if any exception occurs
     */
    public InputStream downloadArtifactsFile(Object projectIdOrPath, String ref, String jobName) throws GitLabApiException {
        Form formData = new GitLabApiForm().withParam("job", jobName, true);
        Response response = getWithAccepts(Response.Status.OK, formData.asMap(), MediaType.MEDIA_TYPE_WILDCARD,
                "projects", getProjectIdOrPath(projectIdOrPath), "jobs", "artifacts", ref, "download");
        return (response.readEntity(InputStream.class));
    }

    /**
     * Download the job artifacts file for the specified job ID.  The artifacts file will be saved in the
     * specified directory with the following name pattern: job-{jobid}-artifacts.zip.  If the file already
     * exists in the directory it will be overwritten.
     *
     * GET /projects/:id/jobs/:job_id/artifacts
     *
     * @param projectIdOrPath id, path of the project, or a Project instance holding the project ID or path
     * @param jobId the job ID to get the artifacts for
     * @param directory the File instance of the directory to save the file to, if null will use "java.io.tmpdir"
     * @return a File instance pointing to the download of the specified job artifacts file
     * @throws GitLabApiException if any exception occurs
     */
    public File downloadArtifactsFile(Object projectIdOrPath, Integer jobId, File directory) throws GitLabApiException {

        Response response = getWithAccepts(Response.Status.OK, null, MediaType.MEDIA_TYPE_WILDCARD,
                "projects", getProjectIdOrPath(projectIdOrPath), "jobs", jobId, "artifacts");
        try {

            if (directory == null)
                directory = new File(System.getProperty("java.io.tmpdir"));

            String filename = "job-" + jobId + "-artifacts.zip";
            File file = new File(directory, filename);

            InputStream in = response.readEntity(InputStream.class);
            Files.copy(in, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return (file);

        } catch (IOException ioe) {
            throw new GitLabApiException(ioe);
        }
    }

    /**
     * Get an InputStream pointing to the job artifacts file for the specified job ID.
     *
     * GET /projects/:id/jobs/:job_id/artifacts
     *
     * @param projectIdOrPath id, path of the project, or a Project instance holding the project ID or path
     * @param jobId the job ID to get the artifacts for
     * @return an InputStream to read the specified job artifacts file
     * @throws GitLabApiException if any exception occurs
     */
    public InputStream downloadArtifactsFile(Object projectIdOrPath, Integer jobId) throws GitLabApiException {
        Response response = getWithAccepts(Response.Status.OK, null, MediaType.MEDIA_TYPE_WILDCARD,
                "projects", getProjectIdOrPath(projectIdOrPath), "jobs", jobId, "artifacts");
        return (response.readEntity(InputStream.class));
    }

    /**
     * Download a single artifact file from within the job's artifacts archive.
     *
     * Only a single file is going to be extracted from the archive and streamed to a client.
     *
     * GET /projects/:id/jobs/:job_id/artifacts/*artifact_path
     *
     * @param projectIdOrPath id, path of the project, or a Project instance holding the project ID or path
     * @param jobId the unique job identifier
     * @param artifactsFile an ArtifactsFile instance for the artifact to download
     * @param directory the File instance of the directory to save the file to, if null will use "java.io.tmpdir"
     * @return a File instance pointing to the download of the specified artifacts file
     * @throws GitLabApiException if any exception occurs
     */
    public File downloadArtifactsFile(Object projectIdOrPath, Integer jobId, ArtifactsFile artifactsFile, File directory) throws GitLabApiException {

        Response response = get(Response.Status.OK, getDefaultPerPageParam(),
                "projects", getProjectIdOrPath(projectIdOrPath), "jobs", jobId, "artifacts", artifactsFile.getFilename());
        try {

            if (directory == null)
                directory = new File(System.getProperty("java.io.tmpdir"));

            String filename = artifactsFile.getFilename();
            File file = new File(directory, filename);

            InputStream in = response.readEntity(InputStream.class);
            Files.copy(in, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return (file);

        } catch (IOException ioe) {
            throw new GitLabApiException(ioe);
        }
    }

    /**
     * Download a single artifact file from within the job's artifacts archive.
     *
     * Only a single file is going to be extracted from the archive and streamed to a client.
     *
     * GET /projects/:id/jobs/:job_id/artifacts/*artifact_path
     *
     * @param projectIdOrPath id, path of the project, or a Project instance holding the project ID or path
     * @param jobId the unique job identifier
     * @param artifactsFile an ArtifactsFile instance for the artifact to download
     * @return an InputStream to read the specified artifacts file from
     * @throws GitLabApiException if any exception occurs
     */
    public InputStream downloadArtifactsFile(Object projectIdOrPath, Integer jobId, ArtifactsFile artifactsFile) throws GitLabApiException {
        Response response = get(Response.Status.OK, getDefaultPerPageParam(),
                "projects", getProjectIdOrPath(projectIdOrPath), "jobs", jobId, "artifacts", artifactsFile.getFilename());
        return (response.readEntity(InputStream.class));
    }

    /**
     * Download a single artifact file from within the job's artifacts archive.
     *
     * Only a single file is going to be extracted from the archive and streamed to a client.
     *
     * GET /projects/:id/jobs/:job_id/artifacts/*artifact_path
     *
     * @param projectIdOrPath id, path of the project, or a Project instance holding the project ID or path
     * @param jobId the unique job identifier
     * @param artifactPath the Path to a file inside the artifacts archive
     * @param directory the File instance of the directory to save the file to, if null will use "java.io.tmpdir"
     * @return a File instance pointing to the download of the specified artifacts file
     * @throws GitLabApiException if any exception occurs
     */
    public File downloadSingleArtifactsFile(Object projectIdOrPath, Integer jobId, Path artifactPath, File directory) throws GitLabApiException {

        Response response = get(Response.Status.OK, getDefaultPerPageParam(), "projects", getProjectIdOrPath(projectIdOrPath), "jobs", jobId, "artifacts", artifactPath);
        try {

            if (directory == null)
                directory = new File(System.getProperty("java.io.tmpdir"));

            String filename = artifactPath.getFileName().toString();
            File file = new File(directory, filename);

            InputStream in = response.readEntity(InputStream.class);
            Files.copy(in, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return (file);

        } catch (IOException ioe) {
            throw new GitLabApiException(ioe);
        }
    }

    /**
     * Download a single artifact file from within the job's artifacts archive.
     *
     * Only a single file is going to be extracted from the archive and streamed to a client.
     *
     * GET /projects/:id/jobs/:job_id/artifacts/*artifact_path
     *
     * @param projectIdOrPath id, path of the project, or a Project instance holding the project ID or path
     * @param jobId the unique job identifier
     * @param artifactPath the Path to a file inside the artifacts archive
     * @return an InputStream to read the specified artifacts file from
     * @throws GitLabApiException if any exception occurs
     */
    public InputStream downloadSingleArtifactsFile(Object projectIdOrPath, Integer jobId, Path artifactPath) throws GitLabApiException {
        Response response = get(Response.Status.OK, getDefaultPerPageParam(), "projects", getProjectIdOrPath(projectIdOrPath), "jobs", jobId, "artifacts", artifactPath);
        return (response.readEntity(InputStream.class));
    }

    /**
     * Get a trace of a specific job of a project
     *
     * GET /projects/:id/jobs/:id/trace
     *
     * @param projectIdOrPath id, path of the project, or a Project instance holding the project ID or path
     *                        to get the specified job's trace for
     * @param jobId the job ID to get the trace for
     * @return a String containing the specified job's trace
     * @throws GitLabApiException if any exception occurs during execution
     */
     public String getTrace(Object projectIdOrPath, int jobId) throws GitLabApiException {
        Response response = get(Response.Status.OK, getDefaultPerPageParam(), "projects", getProjectIdOrPath(projectIdOrPath), "jobs", jobId, "trace");
        return (response.readEntity(String.class));
     }

    /**
     * Cancel specified job in a project.
     *
     * POST /projects/:id/jobs/:job_id/cancel
     *
     * @param projectIdOrPath id, path of the project, or a Project instance holding the project ID or path
     * @param jobId the ID to cancel job
     * @return job instance which just canceled
     * @throws GitLabApiException if any exception occurs during execution
     */
    public Job cancleJob(Object projectIdOrPath, int jobId) throws GitLabApiException {
        GitLabApiForm formData = null;
        Response response = post(Response.Status.CREATED, formData, "projects", getProjectIdOrPath(projectIdOrPath), "jobs", jobId, "cancel");
        return (response.readEntity(Job.class));
    }

    /**
     * Retry specified job in a project.
     *
     * POST /projects/:id/jobs/:job_id/retry
     *
     * @param projectIdOrPath id, path of the project, or a Project instance holding the project ID or path
     * @param jobId the ID to retry job
     * @return job instance which just retried
     * @throws GitLabApiException if any exception occurs during execution
     */
    public Job retryJob(Object projectIdOrPath, int jobId) throws GitLabApiException {
        GitLabApiForm formData = null;
        Response response = post(Status.CREATED, formData, "projects", getProjectIdOrPath(projectIdOrPath), "jobs", jobId, "retry");
        return (response.readEntity(Job.class));
    }

    /**
     * Erase specified job in a project.
     *
     * POST /projects/:id/jobs/:job_id/erase
     *
     * @param projectIdOrPath id, path of the project, or a Project instance holding the project ID or path
     * @param jobId the ID to erase job
     * @return job instance which just erased
     * @throws GitLabApiException if any exception occurs during execution
     */
    public Job eraseJob(Object projectIdOrPath, int jobId) throws GitLabApiException {
        GitLabApiForm formData = null;
        Response response = post(Status.CREATED, formData, "projects", getProjectIdOrPath(projectIdOrPath), "jobs", jobId, "erase");
        return (response.readEntity(Job.class));
    }

    /**
     * Play specified job in a project.
     *
     * POST /projects/:id/jobs/:job_id/play
     *
     * @param projectIdOrPath id, path of the project, or a Project instance holding the project ID or path
     * @param jobId the ID to play job
     * @return job instance which just played
     * @throws GitLabApiException if any exception occurs during execution
     */
    public Job playJob(Object projectIdOrPath, int jobId) throws GitLabApiException {
        GitLabApiForm formData = null;
        Response response = post(Status.CREATED, formData, "projects", getProjectIdOrPath(projectIdOrPath), "jobs", jobId, "play");
        return (response.readEntity(Job.class));
    }
}
