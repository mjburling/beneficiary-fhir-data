variable "env_config" {
  description = "All high-level info for the whole vpc"
  type        = object({ env = string, tags = map(string) })
}

variable "fhir_ami" {
  description = "FHIR server AMI"
  type        = string
}

variable "ssh_key_name" {
  description = "SSH Key"
  type        = string
}

variable "git_branch_name" {
  description = "git branch of beneficiary-fhir-data"
  type        = string
}

variable "git_commit_id" {
  description = "git commit of beneficiary-fhir-data"
  type        = string
}

variable "is_public" {
  description = "If true, open the FHIR data end-point to the public Internet"
  type        = bool
  default     = false
}

variable "dashboard_name" {
  description = "Name of the bfd cloudwatch dashboards"
  type        = string
}

variable "dashboard_namespace" {
  description = "The namespace in which the dashboards live in"
  type        = string
}
